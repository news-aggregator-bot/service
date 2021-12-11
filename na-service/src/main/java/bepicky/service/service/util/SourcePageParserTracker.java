package bepicky.service.service.util;

import bepicky.service.nats.publisher.AdminMessagePublisher;
import bepicky.service.service.ISourcePageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SourcePageParserTracker {

    private final ISourcePageService spService;

    private final AdminMessagePublisher adminPublisher;

    @Value("${source_page.read_fail_limit}")
    private Integer sourcePageReadFailLimit;

    private final Map<Long, Integer> failedTicker = new HashMap<>();

    public SourcePageParserTracker(
        ISourcePageService spService,
        AdminMessagePublisher adminPublisher
    ) {
        this.spService = spService;
        this.adminPublisher = adminPublisher;
    }

    public void track(Long id) {
        failedTicker.remove(id);
    }

    public void failed(Long id) {
        failedTicker.computeIfPresent(id, (aLong, times) -> {
            int oneMoreTime = times + 1;
            if (oneMoreTime > sourcePageReadFailLimit) {
                spService.findById(id).ifPresent(sp -> adminPublisher.publish(
                    "PAGE EMPTY",
                    String.valueOf(sp.getId()),
                    sp.getUrl(),
                    String.valueOf(oneMoreTime)
                ));
            }
            return oneMoreTime;
        });
        failedTicker.putIfAbsent(id, 1);
    }
}
