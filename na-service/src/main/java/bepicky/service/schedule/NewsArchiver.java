package bepicky.service.schedule;

import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.INewsNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RefreshScope
public class NewsArchiver {

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private INewsNoteNotificationService newsNoteNotificationService;

    @Value("${na.schedule.archive.months}")
    private int months;

    @Transactional
    @Scheduled(cron = "${na.schedule.archive.cron:0 0 0 */1 * *}")
    public void archive() {
        log.info("news_note:archive:started");
        newsNoteNotificationService.archiveOld();
        newsNoteService.archiveEarlierThan(months);
        log.info("news_note:archive:completed");
    }
}
