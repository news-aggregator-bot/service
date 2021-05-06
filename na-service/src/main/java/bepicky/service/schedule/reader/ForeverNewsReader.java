package bepicky.service.schedule.reader;

import bepicky.service.entity.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RefreshScope
public class ForeverNewsReader extends AbstractNewsReader {

    @Override
    @Scheduled(initialDelay = 5000, fixedDelay = 10 * 60000)
    public void read() {
        readSources(sourceService.findAllEnabledByFetchPeriod(Source.FetchPeriod.FOREVER));
    }
}
