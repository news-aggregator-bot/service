package bepicky.service.schedule.reader;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
public abstract class AbstractNewsReader {

    @Autowired
    protected ISourceService sourceService;

    @Autowired
    private INewsService newsService;

    @Value("${na.schedule.read.enabled}")
    private boolean enabled;

    public abstract void read();

    protected void readSources(Collection<Source> sources) {
        if (enabled) {
            log.info("news:read:started");
            sources.forEach(this::read);
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
