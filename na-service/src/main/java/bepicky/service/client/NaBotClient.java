package bepicky.service.client;

import bepicky.common.domain.request.NotifyReaderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bepicky-client-bot", url = "na-client-bot:8085", configuration = FeignClientConfiguration.class)
public interface NaBotClient {

    @PutMapping("/notify-reader")
    void notifyReader(@RequestBody NotifyReaderRequest request);

    @GetMapping("/ping")
    void ping();
}
