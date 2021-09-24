package bepicky.service.schedule.parse;

import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.nats.publisher.WebPageParserMsgPublisher;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
public abstract class AbstractNewsParseScheduler {

    @Autowired
    protected ISourceService sourceService;

    @Autowired
    private WebPageParserMsgPublisher parserMsgPublisher;

    @Value("${na.schedule.read.enabled}")
    private boolean enabled;

    public abstract void schedule();

    protected void parseSources(Collection<SourceEntity> sources) {
        if (enabled) {
            log.info("news:read:started");
            sources.forEach(this::parse);
            log.info("news:read:completed");
        }
    }

    @Transactional
    public void parse(SourceEntity source) {
        source.getPages().stream().filter(SourcePageEntity::isEnabled).forEach(parserMsgPublisher::init);
    }

}
