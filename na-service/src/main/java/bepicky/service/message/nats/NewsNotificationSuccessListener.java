package bepicky.service.message.nats;

import bepicky.common.msg.NewsNotificationSuccessMessage;
import bepicky.service.service.INewsNoteNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Slf4j
public class NewsNotificationSuccessListener {
    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private INewsNoteNotificationService notificationService;

    @Value("${topics.news.notification.success}")
    private String newsNotificationSuccess;

    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            try {
                NewsNotificationSuccessMessage ids = om.readValue(msg.getData(), NewsNotificationSuccessMessage.class);
                handle(ids);
            } catch (IOException e) {
                log.error("newsnote:notification:success:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(newsNotificationSuccess);
    }

    public void handle(NewsNotificationSuccessMessage ids) {
        notificationService.sent(ids.getChatId(), ids.getNoteId());
        log.debug("newsnote:notification:success: {}", ids);
    }
}
