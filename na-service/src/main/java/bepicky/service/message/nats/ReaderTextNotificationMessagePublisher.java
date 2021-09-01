package bepicky.service.message.nats;

import bepicky.common.msg.TextMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ReaderTextNotificationMessagePublisher {

    @Autowired
    private Connection connection;

    @Autowired
    private ObjectMapper om;

    @Value("${topics.reader.notification}")
    private String readerNotificationSubject;

    public void publish(Long chatId, String text) {
        TextMessage msg = new TextMessage(chatId, text);

        try {
            connection.publish(
                readerNotificationSubject,
                om.writeValueAsString(msg).getBytes(StandardCharsets.UTF_8)
            );
        } catch (JsonProcessingException ignore) {
        }
    }
}
