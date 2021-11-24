package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsArticle;
import bepicky.service.entity.Category;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bepicky.service.entity.NewsNoteNotification.Link.TAG;

@Service
@Transactional
@Slf4j
public class NewsAggregationService implements INewsAggregationService {

    private static final int MAX_LINK_LENGTH = 251;

    private final ISourcePageService sourcePageService;
    private final INewsNoteService newsNoteService;
    private final IValueNormalisationService normalisationService;
    private final ITagService tagService;
    private final INewsNoteNotificationService noteNotificationService;

    @Value("${na.news.domain-check:true}")
    private boolean checkDomain;

    public NewsAggregationService(
        ISourcePageService sourcePageService,
        INewsNoteService newsNoteService,
        IValueNormalisationService normalisationService,
        ITagService tagService,
        INewsNoteNotificationService noteNotificationService
    ) {
        this.sourcePageService = sourcePageService;
        this.newsNoteService = newsNoteService;
        this.normalisationService = normalisationService;
        this.tagService = tagService;
        this.noteNotificationService = noteNotificationService;
    }

    @Override
    public Set<NewsNote> aggregate(RawNews news) {
        if (news.getArticles().isEmpty()) {
            log.info("aggregation:empty news : " + news.getUrl());
            return Set.of();
        }
        SourcePage sp = sourcePageService.findByUrl(news.getUrl())
            .orElseThrow(() -> new SourceNotFoundException("source page not found " + news.getUrl()));

        Set<RawNewsArticle> filteredArticles = news.getArticles()
            .stream()
            .filter(d -> validLink(sp, d))
            .collect(Collectors.groupingBy(RawNewsArticle::getLink))
            .values()
            .stream()
            .map(sameLinkArticles -> {
                if (sameLinkArticles.size() == 1) {
                    return sameLinkArticles.get(0);
                }
                log.info("aggregation: same link articles: {}", sameLinkArticles);
                return sameLinkArticles.stream()
                    .min(Comparator.comparing(RawNewsArticle::getTitle))
                    .orElse(null);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(RawNewsArticle::getTitle))
            .values()
            .stream()
            .map(sameTitleArticles -> {
                if (sameTitleArticles.size() > 1) {
                    log.info("aggregation: same title articles: {}", sameTitleArticles);
                }
                return sameTitleArticles.get(0);
            }).collect(Collectors.toSet());

        Set<NewsNote> freshArticles = filteredArticles
            .stream()
            .filter(article -> {
                Optional<NewsNote> noteOpt = newsNoteService.findByUrl(article.getLink());
                if (noteOpt.isEmpty()) {
                    return true;
                }
                NewsNote note = noteOpt.get();
                return !note.getSourcePages().contains(sp);
            })
            .map(d -> toNote(sp, d))
            .collect(Collectors.toSet());
        newsNoteService.saveAll(freshArticles);
        return freshArticles;
    }

    @Override
    public Set<NewsNote> aggregateExisting(long latestNoteId) {
        Set<NewsNote> actualNotes = latestNoteId != 0 ?
            newsNoteService.getAllAfter(latestNoteId) :
            newsNoteService.getTodayNotes();

        if (actualNotes.isEmpty()) {
            log.debug("aggregation: existing {} :empty", latestNoteId);
            return Set.of();
        }
        log.info("aggregation: {} articles: start id {} ", actualNotes.size(), latestNoteId);
        actualNotes.stream()
            .collect(Collectors.groupingBy(NewsNote::getSourcePages, Collectors.toSet()))
            .forEach((key, value) -> unfoldSourcePages(key)
                .forEach(r -> noteNotificationService.saveNew(r, value)));
        actualNotes
            .forEach(n -> n.getTags()
                .forEach(t -> t.getReaders()
                    .stream()
                    .filter(r -> atLeastOneInCommon(n.getLanguages(), r.getLanguages()))
                    .forEach(r -> noteNotificationService.saveSingleNew(r, n, TAG, t.getValue()))));
        return actualNotes;
    }

    @Override
    public Set<NewsNote> aggregateLatest(Reader reader) {
//        Set<NewsNote> actualNotes = latestNoteId != 0 ?
//            newsNoteService.getAllAfter(latestNoteId) :
//            newsNoteService.getTodayNotes();
//
//        if (actualNotes.isEmpty()) {
//            log.debug("aggregation: existing {} :empty", latestNoteId);
//            return Set.of();
//        }
//        log.info("aggregation: {} articles: start id {} ", actualNotes.size(), latestNoteId);
//        actualNotes.stream()
//            .collect(Collectors.groupingBy(NewsNote::getSourcePages, Collectors.toSet()))
//            .forEach((key, value) -> unfoldSourcePages(key)
//                .forEach(r -> noteNotificationService.saveNew(r, value)));
        return Set.of();
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
        return true;
    }

    private NewsNote toNote(SourcePage page, RawNewsArticle data) {
        return newsNoteService.findByUrl(data.getLink())
            .map(n -> {
                n.addSourcePage(page);
                return n;
            }).orElseGet(() -> {
                String title = normalisationService.trimTitle(data.getTitle());
                String normTitle = normalisationService.normaliseTitle(title);
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

    private Stream<Reader> unfoldSourcePages(Collection<SourcePage> sps) {
        return sps.stream().flatMap(this::findApplicableReaders);
    }

    private Stream<Reader> findApplicableReaders(SourcePage sp) {
        if (sp.getRegions() == null || sp.getRegions().isEmpty()) {
            return filterReaders(sp, sp.getCategories());
        }
        if (sp.getCategories().size() == 1 && sp.getRegions().size() == 1) {
            return filterReaders(sp, sp.getRegions());
        }
        return filterReaders(sp, sp.getRegions())
            .filter(r -> atLeastOneInCommon(sp.getCommon(), r.getCategories()));
    }

    private Stream<Reader> filterReaders(SourcePage sp, Collection<Category> categories) {
        return categories
            .stream()
            .map(Category::getReaders)
            .flatMap(Set::stream)
            .filter(Reader::isActive)
            .filter(r -> atLeastOneInCommon(sp.getLanguages(), r.getLanguages()))
            .filter(r -> r.getSources().contains(sp.getSource()));
    }

    private <T> boolean atLeastOneInCommon(Collection<T> c1, Collection<T> c2) {
        return c1.stream().anyMatch(c2::contains);
    }
}
