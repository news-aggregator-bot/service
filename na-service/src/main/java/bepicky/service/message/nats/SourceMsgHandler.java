package bepicky.service.message.nats;

import bepicky.common.domain.response.SourceResponse;
import bepicky.common.msg.MsgCommand;
import bepicky.common.msg.SourceCommandMsg;
import bepicky.service.facade.functional.ISourceFunctionalFacade;
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
public class SourceMsgHandler {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ISourceFunctionalFacade srcFacade;

    @Value("${topics.src.cmd}")
    private String srcCommandSubject;

    private Map<MsgCommand, Function<SourceCommandMsg, SourceResponse>> commandMapper =
        ImmutableMap.<MsgCommand, Function<SourceCommandMsg, SourceResponse>>builder()
        .put(MsgCommand.PICK, r -> srcFacade.pick(r))
        .put(MsgCommand.REMOVE, r -> srcFacade.remove(r))
        .build();


    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            long start = System.currentTimeMillis();
            try {
                SourceCommandMsg cmdMsg = om.readValue(msg.getData(), SourceCommandMsg.class);
                SourceResponse response = commandMapper.get(cmdMsg.getCommand()).apply(cmdMsg);
                natsConnection.publish(
                    msg.getReplyTo(),
                    om.writeValueAsString(response).getBytes(StandardCharsets.UTF_8)
                );
                long total = System.currentTimeMillis() - start;
                log.info("source:{}:{}:execution_time:{}", cmdMsg.getSourceId(), cmdMsg.getCommand(), total);
            } catch (IOException e) {
                log.error("source:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(srcCommandSubject);
    }


}
