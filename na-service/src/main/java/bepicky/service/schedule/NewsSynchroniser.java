package bepicky.service.schedule;

import bepicky.service.entity.NewsNote;
import bepicky.service.service.INewsAggregationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Slf4j
public class NewsSynchroniser {

    private final INewsAggregationService aggregationService;

    @Value("${na.schedule.sync.enabled}")
    private boolean syncEnabled;

    private long latestNewsNoteId;

    public NewsSynchroniser(INewsAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @Transactional
    @Scheduled(cron = "${na.schedule.sync.cron}")
    public void sync() {
        if (syncEnabled) {
            Set<NewsNote> actualNotes = aggregationService.aggregateExisting(latestNewsNoteId);
            latestNewsNoteId = actualNotes.stream()
                .mapToLong(NewsNote::getId)
                .max()
                .orElseGet(() -> latestNewsNoteId);
        }
    }
}
