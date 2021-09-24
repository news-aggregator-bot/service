package bepicky.service.schedule.parse;

import bepicky.service.entity.SourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RefreshScope
public class ForeverNewsParseScheduler extends AbstractNewsParseScheduler {

    @Override
    @Scheduled(initialDelay = 5000, fixedDelay = 10 * 60000)
    public void schedule() {
        parseSources(sourceService.findAllEnabledByFetchPeriod(SourceEntity.FetchPeriod.FOREVER));
    }
}
