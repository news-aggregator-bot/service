package bepicky.service.service;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.util.IValueNormalisationService;
import bepicky.service.web.parser.WebContentParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class NewsService implements INewsService {

    private static final int MAX_LINK_LENGTH = 251;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private WebContentParser defaultParser;

    @Autowired
    private IValueNormalisationService normalisationService;

    @Autowired
    private ITagService tagService;

    @Value("${na.news.domain-check:true}")
    private boolean checkDomain;

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
            .collect(Collectors.toSet());
    }

    private Set<NewsNote> syncSource(Source source) {
        return source.getPages()
            .parallelStream()
            .flatMap(this::process)
            .collect(Collectors.toSet());
    }

    private Stream<NewsNote> process(SourcePage page) {
        return defaultParser.parse(page)
            .stream()
            .filter(d -> validLink(page, d))
            .map(d -> toNote(page, d));
    }

    private boolean validLink(SourcePage page, PageParsedData d) {
        if (StringUtils.isBlank(d.getLink())) {
            log.debug("news:skip:empty link:" + d.getLink());
            return false;
        }
        if (d.getLink().length() > MAX_LINK_LENGTH) {
            log.debug("news:skip:long link:" + d.getLink());
            return false;
        }
        if (checkDomain && !d.getLink().contains(page.getHost())) {
            log.debug("news:skip:wrong host:" + d.getLink());
            return false;
        }
        return !newsNoteService.existsByUrl(d.getLink());
    }

    private NewsNote toNote(SourcePage page, PageParsedData data) {
        String title = data.getTitle().trim();
        String normTitle = normalisationService.normaliseTitle(title);
        return newsNoteService.findByNormalisedTitle(normTitle)
            .filter(n -> DateUtils.isSameDay(new Date(), n.getCreationDate()))
            .map(n -> {
                n.addSourcePage(page);
                return n;
            }).orElseGet(() -> {
                NewsNote note = new NewsNote();
                note.setTitle(title);
                note.setNormalisedTitle(normTitle);
                note.setUrl(data.getLink());
                note.setAuthor(data.getAuthor());
                note.addSourcePage(page);
                note.setTags(tagService.findByTitle(title));
                return note;
            });
    }
}
