package bepicky.service.facade;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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
                Stream<Language> sourcePageLangStream = sourcePage.getLanguages().stream();
                Stream<Category> commonCategoriesStream = sourcePage.getCommon().stream();
                NewsSyncResult freshNotes = newsService.sync(sourcePage);
                if (sourcePage.getRegions() != null) {
                    sourcePage.getRegions().stream()
                        .map(Category::getReaders)
                        .flatMap(Set::stream)
                        .filter(r -> sourcePageLangStream.anyMatch(l -> r.getLanguages().contains(l)))
                        .filter(r -> commonCategoriesStream.anyMatch(c -> r.getCategories().contains(c)))
                        .forEach(r -> appendReaderQueue(freshNotes, r));
                } else {
                    commonCategoriesStream
                        .map(Category::getReaders)
                        .flatMap(Set::stream)
                        .filter(r -> sourcePageLangStream.anyMatch(l -> r.getLanguages().contains(l)))
                        .forEach(r -> appendReaderQueue(freshNotes, r));
                }

                log.info("synchronisation:finished:{}", sourcePage.getUrl());
            }
        });
    }

    private void appendReaderQueue(NewsSyncResult freshNotes, Reader r) {
        log.info("synchronisation:reader:{}:queue:add", r.getChatId());
        r.addQueueNewsNote(freshNotes.getNewsNotes());
        readerService.save(r);
    }

}
