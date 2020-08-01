package bepicky.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import bepicky.service.domain.request.NotifyReaderRequest;

@FeignClient(name = "na-bot", configuration = FeignClientConfiguration.class)
public interface NaBotClient {

    @PutMapping("/notify-reader")
    void notifyReader(@RequestBody NotifyReaderRequest request);
}
