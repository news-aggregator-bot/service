package bepicky.service.nats.publisher;

import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AdminMessagePublisher {

    @Autowired
    private Connection natsConnection;

    @Value("${topics.message.admin}")
    private String adminSubject;

    public void publish(String... text) {
        natsConnection.publish(adminSubject, String.join("\n", text).getBytes(StandardCharsets.UTF_8));
    }
}
