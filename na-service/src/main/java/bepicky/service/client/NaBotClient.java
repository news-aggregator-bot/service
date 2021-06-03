package bepicky.service.client;

import bepicky.common.domain.request.NotifyMessageRequest;
import bepicky.common.domain.request.NotifyNewsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bepicky-client-bot", configuration = FeignClientConfiguration.class)
public interface NaBotClient {

    @PutMapping("/notify/news")
    void notifyNews(@RequestBody NotifyNewsRequest request);

    @PutMapping("/notify/message")
    void notifyMessage(@RequestBody NotifyMessageRequest request);

}
