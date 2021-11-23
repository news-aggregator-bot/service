package bepicky.service.service;

import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsArticle;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.entity.TestEntityManager;
import bepicky.service.perf.UtilObjectMapper;
import bepicky.service.service.func.FuncSourceDataIngestor;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.MySQLContainerSupport;
import picky.test.NatsContainerSupport;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
@Testcontainers
@Disabled
public class NewsAggregationBugTestCase implements MySQLContainerSupport,
    NatsContainerSupport {

    private final INewsNoteService newsNoteService;
    private final Connection natsConnection;
    private final ISourceService sourceService;
    private final ISourcePageService sourcePageService;
    private final FuncSourceDataIngestor sourceDataIngestor;

    @Autowired
    public NewsAggregationBugTestCase(
        INewsNoteService newsNoteService,
        Connection natsConnection,
        SourceIngestionService sourceIngestionService,
        ISourceService sourceService,
        ISourcePageService sourcePageService
    ) {
        this.newsNoteService = newsNoteService;
        this.natsConnection = natsConnection;
        this.sourceService = sourceService;
        this.sourcePageService = sourcePageService;
        sourceDataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIngestionService)
            .build();
        sourceDataIngestor.ingestSource("ITC-Source");
    }

    @Value("${topics.news.aggr}")
    private String aggregationSubject;

    private final UtilObjectMapper om = new UtilObjectMapper();

    @Test
    public void aggregate_WhenTheSameArticleComesFromDifferentSourcePages_ShouldStoreOnlySingleArticle() throws InterruptedException {
        Source itc = sourceService.findByName("ITC").get();
        List<SourcePage> itcPages = sourcePageService.findBySource(itc);

        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle(
                "Spotify отключил случайное перемешивание треков после того, как об этом попросила Адель",
                "https://itc.ua/news/spotify-otklyuchil-sluchajnoe-peremeshivanie-trekov-posle-togo-kak-ob-etom-poprosila-adel/"
            )
        );
        for (int i = 0; i < 15; i++) {
            for (SourcePage anyPage : itcPages) {
                RawNews rawNews = TestEntityManager.rawNews(anyPage.getUrl(), rawNewsArticles);
                natsConnection.publish(aggregationSubject, om.writeData(rawNews));
            }
        }
        Thread.sleep(5000);
        Collection<NewsNote> flushed = newsNoteService.flush();
        Assertions.assertEquals(1, flushed.size());

        NewsNote article = flushed.stream().findFirst().get();
        Assertions.assertEquals(itcPages.size(), article.getSourcePages().size());
    }

    @Test
    public void aggregate_SameArticleComesFromDifferentSourcePagesAndFlushedInTheMiddle_ShouldStoreOnlySingleArticle() throws InterruptedException {
        Source itc = sourceService.findByName("ITC").get();
        List<SourcePage> itcPages = sourcePageService.findBySource(itc);

        Set<RawNewsArticle> rawNewsArticles = Set.of(
            TestEntityManager.rawNewsArticle(
                "SameArticleComesFromDifferentSourcePagesAndFlushedInTheMiddle",
                "https://itc.ua/SameArticleComesFromDifferentSourcePagesAndFlushedInTheMiddle"
            )
        );

        for (int i = 0; i < itcPages.size() / 2; i++) {
            SourcePage anyPage = itcPages.get(i);
            RawNews rawNews = TestEntityManager.rawNews(anyPage.getUrl(), rawNewsArticles);
            natsConnection.publish(aggregationSubject, om.writeData(rawNews));
        }
        Thread.sleep(2000);
        Collection<NewsNote> flushedOnce = newsNoteService.flush();

        for (SourcePage anyPage : itcPages) {
            RawNews rawNews = TestEntityManager.rawNews(anyPage.getUrl(), rawNewsArticles);
            natsConnection.publish(aggregationSubject, om.writeData(rawNews));
        }
        Thread.sleep(2000);
        Collection<NewsNote> flushedTwice = newsNoteService.flush();

        Assertions.assertEquals(1, flushedOnce.size());
        Assertions.assertEquals(1, flushedTwice.size());

        NewsNote article1 = flushedOnce.stream().findFirst().get();
        Assertions.assertEquals(itcPages.size() / 2, article1.getSourcePages().size());

        NewsNote article2 = flushedTwice.stream().findFirst().get();
        Assertions.assertEquals(itcPages.size(), article2.getSourcePages().size());
    }
}
