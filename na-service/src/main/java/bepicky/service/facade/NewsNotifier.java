package bepicky.service.facade;

import bepicky.common.domain.request.NotifyNewsRequest;
import bepicky.service.client.NaBotClient;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.entity.Reader;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@Slf4j
@RefreshScope
public class NewsNotifier {

    @Autowired
    private NaBotClient botClient;

    @Autowired
    private IReaderService readerService;

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
            readerService.findAllEnabled().parallelStream()
                .filter(r -> !r.getNotifyQueue().isEmpty())
                .forEach(this::notify);
        } else {
            log.warn("notify:disabled");
        }
    }

    private void notify(Reader r) {
        r.getNotifyQueue().stream()
            .limit(notifyLimit)
            .map(n -> newsNoteDtoMapper.toDto(n, r.getPrimaryLanguage()))
            .map(request -> new NotifyNewsRequest(
                r.getChatId(),
                r.getPrimaryLanguage().getLang(),
                Arrays.asList(request)
            ))
            .forEach(notifyNewsRequest -> {
                try {
                    botClient.notifyNews(notifyNewsRequest);
                    log.info("notify:reader:success {}", r.getChatId());
                    r.removeQueueNewsNote(r.getNotifyQueue());
                    readerService.save(r);
                    log.info("notify:reader:{}:notes:removed", r.getChatId());
                } catch (Exception e) {
                    log.error("notify:reader:fail {} {}", r.getChatId(), e.getMessage());
                }
            });
    }

}
