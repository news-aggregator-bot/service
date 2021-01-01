package bepicky.service.schedule;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@RefreshScope
public class NewsReader {

    @Autowired
    private INewsService newsService;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourcePageService sourcePageService;

    private List<Long> activeSourcesIds;

    private final AtomicInteger sourceNumber = new AtomicInteger(0);

    private final Map<String, AtomicInteger> sources = new HashMap<>();

    @PostConstruct
    public void initSources() {
        sourceService.findAllEnabled().forEach(s -> sources.put(s.getName(), new AtomicInteger(0)));
    }

    @Transactional
    @Scheduled(cron = "${na.schedule.read.cron:*/2 * * * * *}")
    public void read() {
        if (activeSourcesIds == null) {
            refreshIds();
        }
        refreshSourceNumber();

        Long sourceId = activeSourcesIds.get(sourceNumber.getAndIncrement());
        Source source = sourceService.find(sourceId).orElse(null);
        if (source == null) {
            log.warn("synchronisation:source {}:404", sourceId);
            return;
        }
        AtomicInteger sourcePageNum = sources.get(source.getName());

        long sourcePageAmount = sourcePageService.countBySource(source);
        if (sourcePageNum == null || sourcePageNum.get() == sourcePageAmount) {
            AtomicInteger value = new AtomicInteger(0);
            sources.put(source.getName(), value);
            sourcePageNum = value;
            log.debug("synchronisation:source:ended:{}", source.getName());
        }

        PageRequest singlePageRequest = PageRequest.of(sourcePageNum.getAndIncrement(), 1);
        SourcePage sourcePage =
            sourcePageService.findFirstBySource(source, singlePageRequest).orElse(null);
        if (sourcePage == null) {
            return;
        }
        NewsSyncResult freshNotes = newsService.read(sourcePage);
        log.debug("news:read:{}", freshNotes.getNewsNotes().size());
    }

    @Scheduled(cron = "${na.schedule.refresh-id.cron:0 0 */1 * * *}")
    public void refreshIds() {
        activeSourcesIds = sourceService.findAllEnabled().stream().map(Source::getId).collect(Collectors.toList());
        log.debug("news:read:refresh-id:{}", activeSourcesIds);
    }

    private void refreshSourceNumber() {
        if (activeSourcesIds.size() == sourceNumber.get()) {
            log.debug("synchronisation:source number:refresh");
            sourceNumber.set(0);
        }
    }

}
