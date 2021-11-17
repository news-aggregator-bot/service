package bepicky.service.nats.publisher;

import bepicky.service.entity.SourcePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ParseSourcePageMsgPublisher {
    private final Connection connection;
    private final ObjectMapper om;

    @Value("${topics.news.parse}")
    private String parseSubject;

    @Value("${topics.news.aggr}")
    private String aggregateSubject;

    public ParseSourcePageMsgPublisher(
        Connection connection,
        ObjectMapper om
    ) {
        this.connection = connection;
        this.om = om;
    }

    public void publish(SourcePage sp) {
        try {
            connection.publish(
                parseSubject,
                aggregateSubject,
                om.writeValueAsBytes(sp)
            );
        } catch (JsonProcessingException e) {
            log.error("source page: parse : failed", e);
        }
    }
}
