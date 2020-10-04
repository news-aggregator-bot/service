package bepicky.service.facade;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.Category;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@RefreshScope
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

    private List<Long> activeSourcesIds;

    private final AtomicInteger sourceNumber = new AtomicInteger(0);

    private final Map<String, AtomicInteger> sources = new HashMap<>();

    @PostConstruct
    public void initSources() {
        sourceService.findAllActive().forEach(s -> sources.put(s.getName(), new AtomicInteger(0)));
    }

    @Transactional
    @Scheduled(cron = "${na.schedule.sync.cron:*/2 * * * * *}")
    public void sync() {
        if (!syncEnabled) {
            return;
        }
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
        if (sourcePageNum.get() == sourcePageAmount) {
            sources.put(source.getName(), new AtomicInteger(0));
            log.debug("synchronisation:source:ended:{}", source.getName());
        }

        PageRequest singlePageRequest = PageRequest.of(sourcePageNum.getAndIncrement(), 1);
        SourcePage sourcePage =
            sourcePageService.findFirstBySource(source, singlePageRequest).orElse(null);
        if (sourcePage == null) {
            return;
        }
        NewsSyncResult freshNotes = newsService.sync(sourcePage);
        if (freshNotes.getNewsNotes().isEmpty()) {
            log.debug("synchronisation:finished:empty: {}", sourcePage.getUrl());
            return;
        }
        if (sourcePage.getRegions() != null) {
            sourcePage.getRegions().stream()
                .map(Category::getReaders)
                .flatMap(Set::stream)
                .filter(r -> atLeastOneInCommon(sourcePage.getLanguages(), r.getLanguages()))
                .filter(r -> atLeastOneInCommon(sourcePage.getCategories(), r.getCategories()))
                .forEach(r -> appendReaderQueue(freshNotes, r));
        } else {
            sourcePage.getCategories()
                .stream()
                .map(Category::getReaders)
                .flatMap(Set::stream)
                .filter(r -> atLeastOneInCommon(sourcePage.getLanguages(), r.getLanguages()))
                .forEach(r -> appendReaderQueue(freshNotes, r));
        }

        log.debug("synchronisation:finished:{}", sourcePage.getUrl());
    }

    private <T> boolean atLeastOneInCommon(Collection<T> c1, Collection<T> c2) {
        return !Collections.disjoint(c1, c2);
    }

    @Scheduled(cron = "${na.schedule.refresh-id.cron:0 0 */1 * * *}")
    public void refreshIds() {
        activeSourcesIds = sourceService.findAllActive().stream().map(Source::getId).collect(Collectors.toList());
        log.debug("synchronisation:refresh-id:{}", activeSourcesIds);
    }

    private void refreshSourceNumber() {
        if (activeSourcesIds.size() == sourceNumber.get()) {
            log.warn("synchronisation:source number:refresh");
            sourceNumber.set(0);
        }
    }

    private void appendReaderQueue(NewsSyncResult freshNotes, Reader r) {
        log.debug("synchronisation:reader:{}:queue:add", r.getChatId());
        r.addQueueNewsNote(freshNotes.getNewsNotes());
        readerService.save(r);
    }

}
