package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsArticle;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.SourcePage;
import bepicky.service.entity.Tag;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class NewsAggregationService implements INewsAggregationService {

    private static final int MAX_LINK_LENGTH = 251;

    private final ISourcePageService sourcePageService;
    private final INewsNoteService newsNoteService;
    private final IValueNormalisationService normalisationService;
    private final ITagService tagService;

    @Value("${na.news.domain-check:true}")
    private boolean checkDomain;

    public NewsAggregationService(
        ISourcePageService sourcePageService,
        INewsNoteService newsNoteService,
        IValueNormalisationService normalisationService,
        ITagService tagService
    ) {
        this.sourcePageService = sourcePageService;
        this.newsNoteService = newsNoteService;
        this.normalisationService = normalisationService;
        this.tagService = tagService;
    }

    @Override
    public Set<NewsNote> aggregate(RawNews news) {
        if (news.getArticles().isEmpty()) {
            log.info("aggregation:empty news : " + news.getUrl());
            return Set.of();
        }
        SourcePage sp = sourcePageService.findByUrl(news.getUrl())
            .orElseThrow(() -> new SourceNotFoundException("source page not found " + news.getUrl()));
        Set<NewsNote> freshArticles = news.getArticles()
            .stream()
            .filter(d -> validLink(sp, d))
            .map(d -> toNote(sp, d))
            .collect(Collectors.toSet());
        newsNoteService.saveAll(freshArticles);
        return freshArticles;
    }

    private boolean validLink(SourcePage page, RawNewsArticle d) {
        if (StringUtils.isBlank(d.getLink())) {
            log.debug("aggregation:skip:empty link:" + d.getLink());
            return false;
        }
        if (d.getLink().length() > MAX_LINK_LENGTH) {
            log.debug("aggregation:skip:long link:" + d.getLink());
            return false;
        }
        if (checkDomain && !d.getLink().contains(page.getHost())) {
            log.debug("aggregation:skip:wrong host:" + d.getLink());
            return false;
        }
        return !newsNoteService.existsByUrl(d.getLink());
    }

    private NewsNote toNote(SourcePage page, RawNewsArticle data) {
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
