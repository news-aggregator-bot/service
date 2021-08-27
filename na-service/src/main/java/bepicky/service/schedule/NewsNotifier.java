package bepicky.service.schedule;

import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.Reader;
import bepicky.service.message.nats.NatsNewsNotificationPublisher;
import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RefreshScope

public class NewsNotifier {

    @Autowired
    private NatsNewsNotificationPublisher requestProducer;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private INewsNoteNotificationService notificationService;

    @Autowired
    private NewsNoteDtoMapper newsNoteDtoMapper;

    @Value("${na.schedule.notify.enabled}")
    private boolean notifyEnabled;

    @Transactional
    @Scheduled(initialDelay = 5000, fixedDelay = 60000)
    public void sync() {
        if (notifyEnabled) {
            log.info("notify:started");
            readerService.findAllEnabled().stream()
                .map(notificationService::findAllNew)
                .filter(notifications -> !notifications.isEmpty())
                .forEach(this::notify);
            log.info("notify:completed");
        } else {
            log.warn("notify:disabled");
        }
    }

    private void notify(List<NewsNoteNotification> allNotifications) {
        Reader r = allNotifications.get(0).getReader();
        allNotifications.stream()
            .limit(5)
            .map(n -> newsNoteDtoMapper.toNotificationDto(n))
            .forEach(dto -> requestProducer.sendNotification(r.getChatId(), r.getPrimaryLanguage().getLang(), dto));
    }
}
