package vlad110kg.news.aggregator.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vlad110kg.news.aggregator.ErrorUtil;
import vlad110kg.news.aggregator.client.NaBotClient;
import vlad110kg.news.aggregator.domain.request.NewsNoteRequest;
import vlad110kg.news.aggregator.domain.request.NotifyReaderRequest;
import vlad110kg.news.aggregator.domain.request.SourcePageRequest;
import vlad110kg.news.aggregator.domain.response.ErrorResponse;
import vlad110kg.news.aggregator.entity.NewsNote;
import vlad110kg.news.aggregator.entity.Reader;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.service.IReaderService;

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

    @Value("${na.schedule.notify.enabled}")
    private boolean notifyEnabled;

    @Transactional
    @Scheduled(cron = "${na.schedule.notify.cron:* */2 * * * *}")
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
            .map(n -> {
                SourcePageRequest sourcePageRequest = buildSourcePageRequest(n);
                return NewsNoteRequest.builder()
                    .url(n.getUrl())
                    .description(n.getDescription())
                    .author(n.getAuthor())
                    .title(n.getTitle())
                    .sourcePage(sourcePageRequest)
                    .build();
            }).collect(Collectors.toList());
        NotifyReaderRequest notifyRequest = NotifyReaderRequest.builder()
            .chatId(r.getChatId())
            .lang(r.getPrimaryLanguage().getLang())
            .notes(freshNotes)
            .build();

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

    private SourcePageRequest buildSourcePageRequest(NewsNote n) {
        SourcePageRequest sourcePageRequest = new SourcePageRequest();
        SourcePage srcPage = n.getSourcePage();
        sourcePageRequest.setUrl(srcPage.getUrl());
        sourcePageRequest.setName(srcPage.getName());
        sourcePageRequest.setLanguage(srcPage.getLanguage().getLang());
        return sourcePageRequest;
    }

}
