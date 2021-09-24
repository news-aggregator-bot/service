package bepicky.service.nats.publisher;

import bepicky.service.entity.SourcePageEntity;
import bepicky.service.service.INewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picky.data.reader.dto.ParsedSourcePageDto;
import picky.data.reader.dto.SourcePageDto;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Slf4j
public class WebPageParserMsgPublisher {

    private final Connection natsConnection;

    private final ObjectMapper om;

    private final ModelMapper modelMapper;

    @Value("${topics.news.read.start}")
    private String startSubject;

    @Value("${topics.news.read.finish}")
    private String finishSubject;

    public WebPageParserMsgPublisher(
        Connection natsConnection,
        ObjectMapper om,
        ModelMapper modelMapper
    ) {
        this.natsConnection = natsConnection;
        this.om = om;
        this.modelMapper = modelMapper;
    }

    public void init(SourcePageEntity page) {
        try {
            SourcePageDto dto = modelMapper.map(page, SourcePageDto.class);
            natsConnection.publish(startSubject, finishSubject, om.writeValueAsBytes(dto));
            log.info("page:parse:init {}: " + page.getUrl(), page.getId());
        } catch (RuntimeException | JsonProcessingException e) {
            log.warn("page:parse:init:failed:{}", page.getUrl(), e);
        }
    }
}
