package bepicky.service.schedule.parser;

import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.nats.publisher.ParseSourcePageMsgPublisher;
import bepicky.service.service.ISourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
public abstract class AbstractNewsParser {

    @Autowired
    protected ISourceService sourceService;

    @Autowired
    private ParseSourcePageMsgPublisher parserPublisher;

    @Value("${na.schedule.parse.enabled}")
    private boolean enabled;

    public abstract void read();

    protected void parseSources(Collection<Source> sources) {
        if (enabled) {
            log.info("source:parse:started");
            sources.forEach(this::parse);
        }
    }

    @Transactional
    public void parse(Source source) {
        source.getPages().stream().filter(SourcePage::isEnabled).forEach(this::parsePage);
    }

    public void parsePage(SourcePage page) {
        try {
            parserPublisher.publish(page);
        } catch (RuntimeException e) {
            log.warn("source:parse:failed:{}", page.getUrl(), e);
        }
    }
}
