package bepicky.service.perf;

import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.domain.RawNews;
import bepicky.service.repository.NewsNoteRepository;
import bepicky.service.service.NewsAggregationService;
import bepicky.service.service.func.FuncSourceDataIngestor;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import picky.test.MySQLContainerSupport;
import picky.test.NatsContainerSupport;

import java.util.List;

@SpringBootTest
@Slf4j
@Testcontainers
@ExtendWith(MockitoExtension.class)
@Disabled
public class NewsAggregationPerfTest implements MySQLContainerSupport,
    NatsContainerSupport {

    @Autowired
    @InjectMocks
    private NewsAggregationService newsAggregationService;

    @SpyBean
    private NewsNoteRepository newsNoteRepository;

    @Autowired
    private Connection natsConnection;

    @Autowired
    private SourceIngestionService sourceIngestionService;

    @Value("${topics.news.aggr}")
    private String aggregationSubject;

    private final RawNewsContext rawNewsContext = new RawNewsContext();

    private final UtilObjectMapper om = new UtilObjectMapper();

    @Test
    public void perf() throws InterruptedException {
        FuncSourceDataIngestor dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIngestionService)
            .build();

        dataIngestor.ingestSource("Source - ukrpravda");

        List<RawNews> rawNews = rawNewsContext.getRawNews();
        log.info("Perf test aggregating {} pages", rawNews.size());
        for (RawNews raw : rawNews) {
            natsConnection.publish(aggregationSubject, om.writeData(raw));
        }
        Thread.sleep(40000);
        Mockito.verify(newsNoteRepository, Mockito.times(rawNews.size() - 1)).saveAll(Mockito.any());
    }
}
