package bepicky.service.nats.listener;

import bepicky.service.domain.RawNews;
import bepicky.service.service.INewsAggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class RawNewsAggregationListener {
    private final Connection connection;
    private final ObjectMapper om;
    private final INewsAggregationService newsAggregationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${topics.news.aggr}")
    private String aggregateSubject;

    public RawNewsAggregationListener(
        Connection connection,
        ObjectMapper om,
        INewsAggregationService newsAggregationService
    ) {
        this.connection = connection;
        this.om = om;
        this.newsAggregationService = newsAggregationService;
    }

    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = connection.createDispatcher(msg -> executorService.execute(aggregate(msg)));
        dispatcher.subscribe(aggregateSubject);
    }

    public Runnable aggregate(Message msg) {
        return () -> {
            try {
                RawNews rawNews = om.readValue(msg.getData(), RawNews.class);
                newsAggregationService.aggregate(rawNews);
                log.info("aggregation: completed: {}", rawNews.getUrl());
            } catch (IOException | RuntimeException e) {
                log.info("aggregation: failed: {}", msg.getData(), e);
            }
        };
    }
}
