package bepicky.service.schedule.parser;

import bepicky.service.entity.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RefreshScope
public class NineMinNewsParser extends AbstractNewsParser {

    @Override
    @Scheduled(initialDelay = 5000, fixedDelay = 9 * 60000)
    public void read() {
        parseSources(sourceService.findAllEnabledByFetchPeriod(Source.FetchPeriod.NINE_MIN));
    }
}
