package bepicky.service.controller;

import bepicky.service.client.NaBotClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PingController {

    @Autowired
    private NaBotClient botClient;

    @GetMapping("/ping")
    public boolean ping() {
        return true;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void pingBot() {
        try {
            botClient.ping();
            log.info("bepicky-client-bot is online");
        } catch (Exception e) {
            log.warn("bepicky-client-bot is offline");
        }
    }
}
