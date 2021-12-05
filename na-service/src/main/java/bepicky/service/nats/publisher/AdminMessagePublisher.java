package bepicky.service.nats.publisher;

import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AdminMessagePublisher {

    private static final String ID = "aggregation-service";

    private final Connection natsConnection;

    @Value("${topics.message.admin}")
    private String adminSubject;

    public AdminMessagePublisher(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    public void publish(String... text) {
        _publish(String.join("\n", text));
    }

    public void publishInline(String... text) {
        _publish(String.join(" ", text));
    }

    private void _publish(String text) {
        natsConnection.publish(
            adminSubject,
            String.join(ID, text).getBytes(StandardCharsets.UTF_8)
        );
    }
}
