package bepicky.service.schedule;

import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.Reader;
import bepicky.service.nats.publisher.NewsNotificationPublisher;
import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class NewsNotifier {

    private final NewsNotificationPublisher publisher;
    private final IReaderService readerService;
    private final INewsNoteNotificationService notificationService;
    private final NewsNoteDtoMapper newsNoteDtoMapper;

    @Value("${na.schedule.notify.enabled}")
    private boolean notifyEnabled;

    @Value("${na.schedule.notify.limit}")
    private int notifyLimit;

    public NewsNotifier(
        NewsNotificationPublisher publisher,
        IReaderService readerService,
        INewsNoteNotificationService notificationService,
        NewsNoteDtoMapper newsNoteDtoMapper
    ) {
        this.publisher = publisher;
        this.readerService = readerService;
        this.notificationService = notificationService;
        this.newsNoteDtoMapper = newsNoteDtoMapper;
    }

    @Transactional
    @Scheduled(cron = "${na.schedule.notify.cron}")
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
            .limit(notifyLimit)
            .map(newsNoteDtoMapper::toNotificationDto)
            .forEach(dto -> publisher.sendNotification(r.getChatId(), r.getPrimaryLanguage().getLang(), dto));
    }
}
