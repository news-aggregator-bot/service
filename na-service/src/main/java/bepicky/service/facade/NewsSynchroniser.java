package bepicky.service.facade;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NewsSynchroniser {

    @Autowired
    private INewsService newsService;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private IReaderService readerService;

    @Value("${na.schedule.sync.enabled}")
    private boolean syncEnabled;

    private final Map<Source, AtomicInteger> sources = new HashMap<>();

    @Transactional
    @Scheduled(cron = "${na.schedule.sync.cron:*/20 * * * * *}")
    public void sync() {
        if (!syncEnabled) {
            return;
        }
        if (sources.isEmpty()) {
            sourceService.findAll().forEach(s -> sources.put(s, new AtomicInteger(0)));
        }
        Map<Source, AtomicInteger> tempSources = new HashMap<>(sources);
        tempSources.forEach((source, sourcePageNum) -> {

            long sourcePageAmount = sourcePageService.countBySource(source);
            if (sourcePageNum.get() == sourcePageAmount) {
                sources.remove(source);
                log.info("synchronisation:source:ended:{}", source.getName());
                return;
            }

            PageRequest singleElementRequest = PageRequest.of(sourcePageNum.getAndIncrement(), 1);
            SourcePage sourcePage =
                sourcePageService.findFirstBySource(source, singleElementRequest).orElse(null);
            if (sourcePage != null) {
                log.info("synchronisation:started:{}", sourcePage.getUrl());

                NewsSyncResult freshNotes = newsService.sync(sourcePage);
                log.info("synchronisation:collected:{}:{}", sourcePage.getUrl(),
                    freshNotes.getNewsNotes().stream().map(NewsNote::getTitle).collect(Collectors.joining(","))
                );

                log.info("synchronisation:ended:{}", sourcePage.getUrl());
            }
        });
    }

}
