package bepicky.service.client;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.NotifyNewsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bepicky-client-bot", configuration = FeignClientConfiguration.class)
public interface NaBotClient {

    @PutMapping("/notify/news")
    void notifyNews(@RequestBody NotifyNewsRequest request);

    @GetMapping("/ping")
    void ping();

    @PutMapping("/refresh/reply-keyboard")
    void refreshReplyKeyabord(@RequestBody ReaderDto readerDto);
}
