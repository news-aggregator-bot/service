package bepicky.service.schedule;

import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReaderEnabler {

    private final IReaderService readerService;

    public ReaderEnabler(IReaderService readerService) {this.readerService = readerService;}

    @Scheduled(cron = "${na.schedule.enable-sleepers.cron}")
    public void sync() {
        readerService.enableSleeping();
    }
}
