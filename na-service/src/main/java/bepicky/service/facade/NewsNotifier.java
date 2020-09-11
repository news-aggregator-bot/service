package bepicky.service.facade;

import bepicky.common.domain.request.NewsNoteRequest;
import bepicky.common.domain.request.NotifyNewsRequest;
import bepicky.service.client.NaBotClient;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RefreshScope
public class NewsNotifier {

    private static final int NOTES_LIMIT = 1;

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
            readerService.findAllEnabled().parallelStream()
                .filter(r -> r.getNotifyQueue().size() >= NOTES_LIMIT)
                .forEach(this::notify);
        } else {
            log.warn("notify:disabled");
        }
    }

    private void notify(Reader r) {
        Set<NewsNote> freshNotes = r.getNotifyQueue().stream()
            .limit(NOTES_LIMIT)
            .collect(Collectors.toSet());
        List<NewsNoteRequest> notesRequests = freshNotes
            .stream()
            .map(n -> modelMapper.map(n, NewsNoteRequest.class))
            .collect(Collectors.toList());
        NotifyNewsRequest notifyRequest = new NotifyNewsRequest(
            r.getChatId(),
            r.getPrimaryLanguage().getLang(),
            notesRequests
        );

        try {
            botClient.notifyNews(notifyRequest);
            log.info("notify:reader:success {}", r.getChatId());
            r.removeQueueNewsNote(freshNotes);
            readerService.save(r);
            log.info("notify:reader:{}:notes:removed", r.getChatId());
        } catch (Exception e) {
            log.error("notify:reader:fail {} {}", r.getChatId(), e.getMessage());
        }
    }

}
