package bepicky.service.schedule;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.request.NotifyNewsRequest;
import bepicky.service.client.NaBotClient;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.Reader;
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
import java.util.stream.Collectors;

@Component
@Slf4j
@RefreshScope

public class NewsNotifier {

    @Autowired
    private NaBotClient botClient;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private INewsNoteNotificationService notificationService;

    @Autowired
    private NewsNoteDtoMapper newsNoteDtoMapper;

    @Value("${na.schedule.notify.enabled}")
    private boolean notifyEnabled;

    @Value("${na.schedule.notify.limit}")
    private int notifyLimit;

    @Transactional
    @Scheduled(cron = "${na.schedule.notify.cron:0 */2 * * * *}")
    public void sync() {
        if (notifyEnabled) {
            readerService.findAllEnabled().stream()
                .map(notificationService::findNew)
                .filter(notifications -> !notifications.isEmpty())
                .forEach(this::notify);
        } else {
            log.warn("notify:disabled");
        }
    }

    private void notify(List<NewsNoteNotification> allNotifications) {
        List<NewsNoteNotification> notifications = allNotifications.stream()
            .limit(notifyLimit)
            .collect(Collectors.toList());
        Reader r = notifications.get(0).getReader();
        List<NewsNoteDto> dtos = notifications.stream()
            .map(n -> newsNoteDtoMapper.toDto(n.getNote(), r.getPrimaryLanguage()))
            .collect(Collectors.toList());
        NotifyNewsRequest request = new NotifyNewsRequest(
            r.getChatId(),
            r.getPrimaryLanguage().getLang(),
            dtos
        );
        try {
            botClient.notifyNews(request);
            notifications.forEach(notificationService::sent);
            log.info("notify:reader:{}:success", request.getChatId());
        } catch (Exception e) {
            log.error("notify:reader:{}:fail {}", request.getChatId(), e.getMessage());
        }
    }
}
