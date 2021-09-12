package bepicky.service.message.nats;

import bepicky.common.domain.response.LanguageResponse;
import bepicky.common.msg.LanguageCommandMsg;
import bepicky.common.msg.MsgCommand;
import bepicky.service.facade.functional.ILanguageFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class LanguageMsgHandler {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ILanguageFunctionalFacade languageFacade;

    @Value("${topics.lang.cmd}")
    private String langCommandSubject;

    private Map<MsgCommand, Function<LanguageCommandMsg, LanguageResponse>> commandMapper =
        ImmutableMap.<MsgCommand, Function<LanguageCommandMsg, LanguageResponse>>builder()
        .put(MsgCommand.PICK, r -> languageFacade.pick(r))
        .put(MsgCommand.REMOVE, r -> languageFacade.remove(r))
        .build();


    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            long start = System.currentTimeMillis();
            try {
                LanguageCommandMsg cmdMsg = om.readValue(msg.getData(), LanguageCommandMsg.class);
                LanguageResponse response = commandMapper.get(cmdMsg.getCommand()).apply(cmdMsg);
                natsConnection.publish(
                    msg.getReplyTo(),
                    om.writeValueAsString(response).getBytes(StandardCharsets.UTF_8)
                );
                long total = System.currentTimeMillis() - start;
                log.info("lang:{}:{}:execution_time:{}", cmdMsg.getLang(), cmdMsg.getCommand(), total);
            } catch (IOException e) {
                log.error("lang:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(langCommandSubject);
    }


}
