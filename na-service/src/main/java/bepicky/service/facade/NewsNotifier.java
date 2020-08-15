package bepicky.service.facade;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.request.NewsNoteRequest;
import bepicky.common.domain.request.NotifyReaderRequest;
import bepicky.common.domain.response.ErrorResponse;
import bepicky.service.client.NaBotClient;
import bepicky.service.entity.Reader;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NewsNotifier {

    @Autowired
    private NaBotClient botClient;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${na.schedule.notify.enabled}")
    private boolean notifyEnabled;

    @Transactional
    @Scheduled(cron = "${na.schedule.notify.cron:0 */2 * * * *}")
    public void sync() {
        if (notifyEnabled) {
            log.info("notify:reader:start");
            readerService.findAllEnabled().stream()
                .filter(r -> !r.getNotifyQueue().isEmpty())
                .forEach(this::notify);
            log.info("notify:reader:complete");
        }
    }

    private void notify(Reader r) {
        List<NewsNoteRequest> freshNotes = r.getNotifyQueue().stream()
            .map(n -> modelMapper.map(n, NewsNoteRequest.class))
            .collect(Collectors.toList());
        NotifyReaderRequest notifyRequest = new NotifyReaderRequest(
            r.getChatId(),
            r.getPrimaryLanguage().getLang(),
            freshNotes
        );

        CompletableFuture.runAsync(() -> botClient.notifyReader(notifyRequest))
            .whenComplete((u, e) -> {
                if (e == null) {
                    log.info("notify:reader:success {}", r.getChatId());
                } else {
                    ErrorResponse errorResponse = ErrorUtil.parseError(e.getMessage());
                    log.error("notify:reader:fail {} {}", r.getChatId(), e.getMessage());
                    if (errorResponse.isReaderInactive()) {
                        readerService.disable(r.getChatId());
                    }
                }
            });
    }

}
