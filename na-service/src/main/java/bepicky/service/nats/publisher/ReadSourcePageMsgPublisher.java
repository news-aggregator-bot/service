package bepicky.service.nats.publisher;

import bepicky.service.entity.SourcePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ReadSourcePageMsgPublisher {

    private final Connection connection;

    private final ObjectMapper om;

    @Value("${topics.page.read}")
    private String readSubject;
    @Value("${topics.page.parse}")
    private String parseSubject;

    public ReadSourcePageMsgPublisher(
        Connection connection,
        ObjectMapper om
    ) {
        this.connection = connection;
        this.om = om;
    }

    public void publish(SourcePage sp) {
        try {
            connection.publish(
                readSubject,
                parseSubject,
                om.writeValueAsString("msg").getBytes(StandardCharsets.UTF_8)
            );
        } catch (JsonProcessingException ignore) {
        }
    }
}
