package vlad110kg.news.aggregator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad110kg.news.aggregator.domain.NewsSyncResult;
import vlad110kg.news.aggregator.domain.PageParsedData;
import vlad110kg.news.aggregator.entity.NewsNote;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.exception.SourceNotFoundException;
import vlad110kg.news.aggregator.web.parser.WebContentParser;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class NewsService implements INewsService {

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private WebContentParser defaultParser;

    @Override
    public NewsSyncResult sync(String name) {
        Source source = sourceService.findByName(name).orElseThrow(SourceNotFoundException::new);
        Set<NewsNote> savedNotes = new HashSet<>(newsNoteService.saveAll(syncSource(source)));
        return NewsSyncResult.builder().newsNotes(savedNotes).build();
    }

    @Override
    public NewsSyncResult sync(SourcePage sourcePage) {
        Set<NewsNote> freshNews = readFreshNews(sourcePage);
        Set<NewsNote> savedNotes = new HashSet<>(newsNoteService.saveAll(freshNews));
        return NewsSyncResult.builder().newsNotes(savedNotes).build();
    }

    @Override
    public Set<NewsNote> readFreshNews(SourcePage sourcePage) {
        return process(sourcePage)
            .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(NewsNote::getUrl))));
    }

    private Set<NewsNote> syncSource(Source source) {
        return source.getPages()
            .parallelStream()
            .flatMap(this::process)
            .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(NewsNote::getUrl))));
    }

    private Stream<NewsNote> process(SourcePage page) {
        return page.getContentBlocks()
            .parallelStream()
            .map(tag -> defaultParser.parse(page, tag))
            .flatMap(List::stream)
            .filter(d -> !newsNoteService.exists(d.getLink()))
            .map(d -> toNote(page, d));
    }

    private NewsNote toNote(SourcePage page, PageParsedData data) {
        NewsNote note = new NewsNote();
        note.setTitle(data.getTitle());
        note.setUrl(data.getLink());
        note.setDescription(data.getDescription());
        note.setAuthor(data.getAuthor());
        note.setSourcePage(page);
        return note;
    }
}
