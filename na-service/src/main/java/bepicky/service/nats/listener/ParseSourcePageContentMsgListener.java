package bepicky.service.nats.listener;

import bepicky.common.msg.NewsNotificationSuccessMessage;
import bepicky.service.service.INewsAggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ParseSourcePageContentMsgListener {

    private final Connection connection;

    private final ObjectMapper om;

    private final INewsAggregationService newsAggregationService;

    @Value("${topics.page.parse}")
    private String parseSubject;

    public ParseSourcePageContentMsgListener(
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
        Dispatcher dispatcher = connection.createDispatcher(msg -> {
            newsAggregationService.aggregate("", List.of());
        });
        dispatcher.subscribe(parseSubject);
    }
}
