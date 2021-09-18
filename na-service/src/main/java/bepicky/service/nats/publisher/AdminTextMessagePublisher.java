package bepicky.service.nats.publisher;

import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AdminTextMessagePublisher {

    @Autowired
    private Connection natsConnection;

    @Value("${topics.message.admin}")
    private String textMessageSubject;

    public void publish(String text) {
        natsConnection.publish(textMessageSubject, text.getBytes(StandardCharsets.UTF_8));
    }
}
