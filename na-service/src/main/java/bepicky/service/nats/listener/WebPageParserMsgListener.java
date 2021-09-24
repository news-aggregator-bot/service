package bepicky.service.nats.listener;

import bepicky.service.service.INewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picky.data.reader.dto.ParsedSourcePageDto;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Slf4j
public class WebPageParserMsgListener {

    @Autowired
    private INewsService newsService;

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Value("${topics.news.read.finish}")
    private String finishSubject;

    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            try {
                ParsedSourcePageDto dto = om.readValue(msg.getData(), ParsedSourcePageDto.class);
                log.info("page:parse:finish: {}", dto);
                newsService.handleParsed(dto);
            } catch (IOException e) {
                log.error("page:parse:finish:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(finishSubject);
    }
}
