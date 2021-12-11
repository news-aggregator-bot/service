package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsArticle;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.entity.TestEntityManager;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.util.SourcePageParserTracker;
import bepicky.service.service.util.ValueNormalisationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static bepicky.service.entity.TestEntityManager.common;
import static bepicky.service.entity.TestEntityManager.en;
import static bepicky.service.entity.TestEntityManager.note;
import static bepicky.service.entity.TestEntityManager.page;
import static bepicky.service.entity.TestEntityManager.reader;
import static bepicky.service.entity.TestEntityManager.region;
import static bepicky.service.entity.TestEntityManager.source;
import static bepicky.service.entity.TestEntityManager.ua;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NewsAggregationServiceTest {

    private static final String SP_URL = "url";

    private static INewsAggregationService aggregationService;
    private static ISourcePageService sourcePageService;
    private static ITagService tagService;
    private static INewsNoteService newsNoteService;
    private static INewsNoteNotificationService notificationService;
    private static SourcePageParserTracker sourcePageParserTracker;

    @Captor
    private ArgumentCaptor<Set<NewsNote>> notificationsAC;

    @BeforeAll
    public static void initAggregationService() {
        sourcePageService = Mockito.mock(ISourcePageService.class);
        tagService = Mockito.mock(ITagService.class);
        newsNoteService = Mockito.mock(INewsNoteService.class);
        notificationService = Mockito.mock(INewsNoteNotificationService.class);
        sourcePageParserTracker = Mockito.mock(SourcePageParserTracker.class);

        aggregationService = new NewsAggregationService(
            sourcePageService,
            newsNoteService,
            new ValueNormalisationService(),
            tagService,
            notificationService,
            sourcePageParserTracker
        );
    }

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this.getClass());

        Mockito.when(newsNoteService.saveAll(Mockito.any())).thenReturn(Set.of());
        SourcePage page = TestEntityManager.page(SP_URL);
        Mockito.when(sourcePageService.findByUrl(SP_URL)).thenReturn(Optional.of(page));
        Mockito.when(tagService.findByTitle(Mockito.anyString())).thenReturn(Set.of());
    }

    @Test
    public void aggregate_WhenNoArticles_ShouldReturnEmptyList() {
        RawNews rawNews = TestEntityManager.rawNews("url", Set.of());

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(0, aggregated.size());
    }

    @Test
    public void aggregate_WhenNoSourcePageFound_ShouldThrowAnException() {
        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle("title", "any.com")
        );
        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);
        Mockito.when(sourcePageService.findByUrl(SP_URL)).thenReturn(Optional.empty());

        SourceNotFoundException sourceNotFoundException = Assertions.assertThrows(
            SourceNotFoundException.class,
            () -> aggregationService.aggregate(rawNews)
        );

        Assertions.assertEquals("source page not found url", sourceNotFoundException.getMessage());
    }

    @Test
    public void aggregate_WhenValidArticle_ShouldNormaliseTitle() {
        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle("Влада Шрі-Ланки: загиблих у результаті вибухів менше, ніж повідомлялось раніше", "any.com")
        );
        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(1, aggregated.size());
        NewsNote actual = aggregated.stream().findFirst().get();
        Assertions.assertEquals("Влада Шрі-Ланки: загиблих у результаті вибухів менше, ніж повідомлялось раніше", actual.getTitle());
        Assertions.assertEquals("влада шріланки загиблих у результаті вибухів менше ніж повідомлялось раніше", actual.getNormalisedTitle());
        Assertions.assertEquals("https://any.com", actual.getUrl());
    }

    @Test
    public void aggregate_SameArticleAggregatesFromSameSourcePageMultipleTimes_ShouldSkipSecondAggregation() {
        RawNewsArticle rawArticle = TestEntityManager.rawNewsArticle(
            "title",
            "SameArticleAggregatesFromSameSourcePageMultipleTimes.com"
        );
        Set<RawNewsArticle> rawNewsArticles = Set.of(rawArticle);
        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        Set<NewsNote> aggregatedOnce = aggregationService.aggregate(rawNews);
        Assertions.assertEquals(1, aggregatedOnce.size());
        NewsNote actual = aggregatedOnce.stream().findFirst().get();
        when(newsNoteService.existsByUrl(rawArticle.getLink())).thenReturn(true);
        when(newsNoteService.findByUrl(rawArticle.getLink())).thenReturn(List.of(actual));

        Set<NewsNote> aggregatedTwice = aggregationService.aggregate(rawNews);
        Assertions.assertEquals(0, aggregatedTwice.size());

        Assertions.assertEquals("title", actual.getTitle());
        Assertions.assertEquals("title", actual.getNormalisedTitle());
        Assertions.assertEquals(rawArticle.getLink(), actual.getUrl());
    }

    @Test
    public void aggregate_SameArticleAggregatesFromDifferentSourcePage_ShouldAggregateBoth() {
        RawNewsArticle rawArticle = TestEntityManager.rawNewsArticle(
            "SameArticleAggregatesFromDifferentSourcePage",
            "SameArticleAggregatesFromDifferentSourcePage"
        );
        Set<RawNewsArticle> rawNewsArticles = Set.of(rawArticle);

        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        SourcePage page = TestEntityManager.page("new_url");
        Mockito.when(sourcePageService.findByUrl(page.getUrl())).thenReturn(Optional.of(page));
        RawNews rawNews2 = TestEntityManager.rawNews(page.getUrl(), rawNewsArticles);

        Set<NewsNote> aggregatedOnce = aggregationService.aggregate(rawNews);
        Assertions.assertEquals(1, aggregatedOnce.size());
        NewsNote actualOne = aggregatedOnce.stream().findFirst().get();
        when(newsNoteService.findByUrl(rawArticle.getLink())).thenReturn(List.of(actualOne));

        Set<NewsNote> aggregatedTwice = aggregationService.aggregate(rawNews2);
        Assertions.assertEquals(1, aggregatedTwice.size());
        NewsNote actualTwo = aggregatedTwice.stream().findFirst().get();
        when(newsNoteService.findByUrl(rawArticle.getLink())).thenReturn(List.of(actualTwo));

        Assertions.assertEquals("SameArticleAggregatesFromDifferentSourcePage", actualTwo.getTitle());
        Assertions.assertEquals("samearticleaggregatesfromdifferentsourcepage", actualTwo.getNormalisedTitle());
        Assertions.assertEquals(rawArticle.getLink(), actualTwo.getUrl());
    }


    @Test
    public void aggregate_WhenArticlesWithSameTitleAndLinksAreProvided_ShouldAggregateOnlyUniqueArticle() {
        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle("title", "any.com"),
            TestEntityManager.rawNewsArticle("title", "any1.com"),
            TestEntityManager.rawNewsArticle("title1", "any.com")
        );
        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(1, aggregated.size());
        NewsNote actual = aggregated.stream().findFirst().get();
        Assertions.assertEquals("title", actual.getTitle());
        Assertions.assertEquals("title", actual.getNormalisedTitle());
        Assertions.assertEquals("https://any.com", actual.getUrl());
    }

    @Test
    public void aggregate_WhenSameArticlesButDifferentDate_ShouldAggregateAsNewArticle() {
        NewsNote yesterdayNote = TestEntityManager.note("title", TestEntityManager.beforeD(1));
        Mockito.when(newsNoteService.findByNormalisedTitle(yesterdayNote.getTitle()))
            .thenReturn(Optional.of(yesterdayNote));

        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle(yesterdayNote.getTitle(), "any.com")
        );

        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(1, aggregated.size());
        NewsNote actual = aggregated.stream().findFirst().get();
        Assertions.assertEquals("title", actual.getTitle());
        Assertions.assertEquals("title", actual.getNormalisedTitle());
        Assertions.assertEquals("https://any.com", actual.getUrl());
    }

    @Test
    public void aggregate_WhenSameArticlesAndDate_ShouldAggregateAsExistingArticle() {
        NewsNote todayNote = TestEntityManager.note("title", new Date());
        todayNote.addSourcePage(TestEntityManager.page("https://any_url.com"));

        Mockito.when(newsNoteService.findByNormalisedTitle(todayNote.getTitle()))
            .thenReturn(Optional.of(todayNote));

        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle(todayNote.getTitle(), "any.com")
        );

        RawNews rawNews = TestEntityManager.rawNews("url", rawNewsArticles);

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(1, aggregated.size());
        NewsNote actual = aggregated.stream().findFirst().get();
        Assertions.assertEquals("title", actual.getTitle());
        Assertions.assertEquals("title", actual.getNormalisedTitle());
        Assertions.assertEquals("https://any.com", actual.getUrl());
        Assertions.assertEquals(1, todayNote.getSourcePages().size());
    }

    @Test
    public void aggregate_WhenArticlesWithInvalidLinks_ShouldSkipTheirAggregation() {
        Set<RawNewsArticle> invalidLinkArticles = Set.of(
            TestEntityManager.rawNewsArticle("1", ""),
            TestEntityManager.rawNewsArticle("2", null),
            TestEntityManager.rawNewsArticle("3", StringUtils.repeat("a", 252))
        );
        RawNews rawNews = TestEntityManager.rawNews("url", invalidLinkArticles);

        Set<NewsNote> aggregated = aggregationService.aggregate(rawNews);

        Assertions.assertEquals(0, aggregated.size());
    }

    @Test
    public void sync_ApplicableNotes_ShouldSaveAllNotes() {
        Source src = source("name");
        Language en = en();
        Reader r1 = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of(r1));
        Category c1 = common("c1", Set.of(r1));
        List<Category> categories = List.of(uk, c1);
        r1.setCategories(Set.of(uk, c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        when(newsNoteService.getTodayNotes()).thenReturn(notes);

        aggregationService.aggregateExisting(0);

        verify(notificationService, times(1)).saveNew(eq(r1), notificationsAC.capture());

        Set<NewsNote> queue = notificationsAC.getValue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
    }

    @Test
    public void sync_NotApplicableNotesByReaderLanguage_ShouldSaveOnlyForApplicableReaders() {
        Source src = source("name");
        Language en = en();
        Language ua = ua();
        Reader applicable = reader(en, Set.of(en), Set.of(src));
        Reader notApplicable = reader(ua, Set.of(ua), Set.of(src));
        Set<Reader> readers = Set.of(applicable, notApplicable);
        Category uk = region("UK", readers);
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        applicable.setCategories(Set.of(uk, c1));
        notApplicable.setCategories(Set.of(uk, c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        when(newsNoteService.getTodayNotes()).thenReturn(notes);

        aggregationService.aggregateExisting(0);

        verify(notificationService, times(1)).saveNew(eq(applicable), notificationsAC.capture());
        verify(notificationService, never()).saveNew(eq(notApplicable), anySet());

        Set<NewsNote> queue = notificationsAC.getValue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
    }

    @Test
    public void sync_NotApplicableNotesByReaderRegion_ShouldNotSaveAnyNotes() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<Reader> readers = Set.of(notApplicable);
        Category uk = region("UK", Set.of());
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        when(newsNoteService.getAllAfter(any())).thenReturn(notes);

        aggregationService.aggregateExisting(1);

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_NotApplicableNotesByReaderCategory_ShouldNotSaveAnyNotes() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<Reader> readers = Set.of(notApplicable);
        Category uk = region("UK", readers);
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(uk));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        when(newsNoteService.getAllAfter(any())).thenReturn(notes);

        aggregationService.aggregateExisting(1);

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_EmptyCategoryReaders_ShouldNotSaveNotApplicableNewsNotes() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of());
        List<Category> categories = List.of(uk);
        notApplicable.setCategories(Set.of(uk));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        when(newsNoteService.getAllAfter(any())).thenReturn(notes);

        aggregationService.aggregateExisting(1);

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_EmptyActualNotes_ShouldNotSaveNotApplicableNewsNotes() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of());
        notApplicable.setCategories(Set.of(uk));

        when(newsNoteService.getAllAfter(any())).thenReturn(Set.of());

        aggregationService.aggregateExisting(1);

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }
}