package bepicky.service.schedule.reader;

import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.nats.publisher.ReadSourcePageMsgPublisher;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
public abstract class AbstractNewsReader {

    @Autowired
    protected ISourceService sourceService;

    @Autowired
    private ReadSourcePageMsgPublisher readSourcePageMsgPublisher;

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
        source.getPages().stream().filter(SourcePage::isEnabled).forEach(this::readPage);
    }

    public void readPage(SourcePage page) {
        try {
            readSourcePageMsgPublisher.publish(page);
        } catch (RuntimeException e) {
            log.warn("news:read:page:failed:{}", page.getUrl(), e);
        }
    }
}
