package bepicky.service.service;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.util.IValueNormalisationService;
import bepicky.service.web.parser.WebContentParser;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
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

    @Autowired
    private IValueNormalisationService normalisationService;

    @Override
    public NewsSyncResult sync(String name) {
        Source source = sourceService.findByName(name).orElseThrow(SourceNotFoundException::new);
        Set<NewsNote> freshNews = syncSource(source);
        Set<NewsNote> savedNotes = freshNews.isEmpty() ? freshNews : new HashSet<>(newsNoteService.saveAll(freshNews));
        return NewsSyncResult.builder().newsNotes(savedNotes).build();
    }

    @Override
    public NewsSyncResult read(SourcePage sourcePage) {
        Set<NewsNote> freshNews = readFreshNews(sourcePage);
        Set<NewsNote> savedNotes = freshNews.isEmpty() ? freshNews : new HashSet<>(newsNoteService.saveAll(freshNews));
        return NewsSyncResult.builder().newsNotes(savedNotes).build();
    }

    @Override
    public Set<NewsNote> readFreshNews(SourcePage sourcePage) {
        return process(sourcePage)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private Set<NewsNote> syncSource(Source source) {
        return source.getPages()
            .parallelStream()
            .flatMap(this::process)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private Stream<NewsNote> process(SourcePage page) {
        return defaultParser.parse(page)
            .stream()
            .filter(d -> !newsNoteService.existsByUrl(d.getLink()))
            .map(d -> toNote(page, d));
    }

    private NewsNote toNote(SourcePage page, PageParsedData data) {
        String normTitle = normalisationService.normaliseTitle(data.getTitle());
        return newsNoteService.findByNormalisedTitle(normTitle)
            .filter(n -> DateUtils.isSameDay(new Date(), n.getCreationDate()))
            .map(n -> {
                n.addSourcePage(page);
                return n;
            }).orElseGet(() -> {
                NewsNote note = new NewsNote();
                note.setTitle(data.getTitle());
                note.setNormalisedTitle(normTitle);
                note.setUrl(data.getLink());
                note.setAuthor(data.getAuthor());
                note.addSourcePage(page);
                return note;
            });
    }
}
