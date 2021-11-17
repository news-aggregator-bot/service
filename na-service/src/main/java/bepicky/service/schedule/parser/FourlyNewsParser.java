package bepicky.service.schedule.parser;

import bepicky.service.entity.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RefreshScope
public class FourlyNewsParser extends AbstractNewsParser {

    @Override
    @Scheduled(cron = "0 0 */4 * * *")
    public void read() {
        parseSources(sourceService.findAllEnabledByFetchPeriod(Source.FetchPeriod.FOURLY));
    }
}
