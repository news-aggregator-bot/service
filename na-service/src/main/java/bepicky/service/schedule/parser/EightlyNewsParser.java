package bepicky.service.schedule.parser;

import bepicky.service.entity.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EightlyNewsParser extends AbstractNewsParser {

    @Override
    @Scheduled(cron = "0 0 */8 * * *")
    public void read() {
        parseSources(sourceService.findAllEnabledByFetchPeriod(Source.FetchPeriod.EIGHTLY));
    }
}
