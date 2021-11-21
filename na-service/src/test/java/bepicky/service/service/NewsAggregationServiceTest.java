package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsArticle;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.SourcePage;
import bepicky.service.entity.TestEntityManager;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.util.ValueNormalisationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class NewsAggregationServiceTest {

    private static final String SP_URL = "url";

    private static INewsAggregationService aggregationService;
    private static ISourcePageService sourcePageService;
    private static ITagService tagService;
    private static INewsNoteService newsNoteService;

    @BeforeAll
    public static void initAggregationService() {
        sourcePageService = Mockito.mock(ISourcePageService.class);
        tagService = Mockito.mock(ITagService.class);
        newsNoteService = Mockito.mock(INewsNoteService.class);

        aggregationService = new NewsAggregationService(
            sourcePageService,
            newsNoteService,
            new ValueNormalisationService(),
            tagService
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
        Assertions.assertEquals("url", actual.getUrl());
        Assertions.assertEquals(2, todayNote.getSourcePages().size());
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
}