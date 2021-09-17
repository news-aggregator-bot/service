package bepicky.service.nats.listener;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.msg.LanguageCommandMsg;
import bepicky.service.facade.functional.IReaderFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class PrimaryLanguageMsgHandler {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IReaderFunctionalFacade readerFunctionalFacade;

    @Value("${topics.reader.language}")
    private String readerLangSubject;

    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            long start = System.currentTimeMillis();
            try {
                LanguageCommandMsg cmdMsg = om.readValue(msg.getData(), LanguageCommandMsg.class);
                ReaderDto dto = readerFunctionalFacade.changeLanguage(
                    cmdMsg.getChatId(),
                    cmdMsg.getLang()
                );
                if (dto == null) {
                    return;
                }
                natsConnection.publish(
                    msg.getReplyTo(),
                    om.writeValueAsString(dto).getBytes(StandardCharsets.UTF_8)
                );
                long total = System.currentTimeMillis() - start;
                log.info("reader:primary lang:{}:{}:execution_time:{}", cmdMsg.getChatId(), cmdMsg.getLang(), total);
            } catch (IOException e) {
                log.error("reader:primary lang:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(readerLangSubject);
    }
}
