package vlad110kg.news.aggregator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vlad110kg.news.aggregator.domain.request.NotifyReaderRequest;

@FeignClient(name = "na-bot", configuration = FeignClientConfiguration.class)
public interface NaBotClient {

    @PutMapping("/notify-reader")
    void notifyReader(@RequestBody NotifyReaderRequest request);
}
