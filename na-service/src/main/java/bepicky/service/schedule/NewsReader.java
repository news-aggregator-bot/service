package bepicky.service.schedule;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RefreshScope
public class NewsReader {

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private INewsService newsService;

    @Value("${na.schedule.read.enabled}")
    private boolean enabled;

    @Scheduled(initialDelay = 5000, fixedDelay = 10 * 60000)
    public void readAll() {
        if (enabled) {
            log.info("news:read:started");
            sourceService.findAllEnabled().forEach(this::read);
        }
    }

    @Transactional
    public void read(Source source) {
        source.getPages().forEach(this::readPage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void readPage(SourcePage page) {
        try {
            NewsSyncResult freshNotes = newsService.read(page);
            log.debug("news:read:{}", freshNotes.getNewsNotes().size());
        } catch (RuntimeException e) {
            log.warn("news:read:page:{}:failed", page.getUrl(), e);
        }
    }
}
