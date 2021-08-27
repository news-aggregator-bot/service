package bepicky.service.message.nats;

import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.common.domain.request.NewsNotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class NatsNewsNotificationRequestProducer {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Value("${topics.news.notification.new}")
    private String newsNotificationTopic;

    public void sendNotification(
        Long chatId, String lang, NewsNoteNotificationDto dto
    ) {
        NewsNotificationRequest request = new NewsNotificationRequest(
            chatId,
            lang,
            dto
        );
        try {
//            JetStream js = natsConnection.jetStream();
            natsConnection.publish(
                newsNotificationTopic,
                om.writeValueAsString(request)
                    .getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            log.error("newsnote:notification:failed " + e.getMessage());
        }
    }
}
