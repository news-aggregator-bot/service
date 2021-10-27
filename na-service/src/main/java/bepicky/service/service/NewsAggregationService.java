package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsNote;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.SourcePage;
import bepicky.service.entity.Tag;
import bepicky.service.service.util.IValueNormalisationService;
import bepicky.service.web.parser.WebContentParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
public class NewsAggregationService implements INewsAggregationService {

    private static final int MAX_LINK_LENGTH = 251;

    private final ISourcePageService sourcePageService;
    private final INewsNoteService newsNoteService;
    private final WebContentParser defaultParser;
    private final IValueNormalisationService normalisationService;
    private final ITagService tagService;

    @Value("${na.news.domain-check:true}")
    private boolean checkDomain;

    public NewsAggregationService(
        ISourcePageService sourcePageService,
        INewsNoteService newsNoteService,
        WebContentParser defaultParser,
        IValueNormalisationService normalisationService,
        ITagService tagService
    ) {
        this.sourcePageService = sourcePageService;
        this.newsNoteService = newsNoteService;
        this.defaultParser = defaultParser;
        this.normalisationService = normalisationService;
        this.tagService = tagService;
    }

    @Override
    public Set<NewsNote> aggregate(String url, List<String> content) {
        return sourcePageService.findByUrl(url)
            .map(sp -> process(sp, content))

            .orElse(Set.of());
    }

    private Stream<NewsNote> process(SourcePage page, List<String> content) {
        RawNews rawNews = defaultParser.parse(page, content);
        if (rawNews.getNotes().isEmpty()) {
            return Stream.empty();
        }
        return rawNews.getNotes()
            .stream()
            .filter(d -> validLink(page, d))
            .map(d -> toNote(page, d));
    }

    private boolean validLink(SourcePage page, RawNewsNote d) {
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

    private NewsNote toNote(SourcePage page, RawNewsNote data) {
        String title = normalisationService.trimTitle(data.getTitle());
        String normTitle = normalisationService.normaliseTitle(title);
        return newsNoteService.findByNormalisedTitle(normTitle)
            .filter(n -> DateUtils.isSameDay(new Date(), n.getCreationDate()))
            .map(n -> {
                n.addSourcePage(page);
                return n;
            }).orElseGet(() -> {
                NewsNote note = new NewsNote();
                Set<Tag> tagsTitle = tagService.findByTitle(title);
                note.setTitle(title);
                note.setNormalisedTitle(normTitle);
                note.setUrl(data.getLink());
                note.setAuthor(data.getAuthor());
                note.addSourcePage(page);
                note.setTags(tagsTitle);
                return note;
            });
    }
}
