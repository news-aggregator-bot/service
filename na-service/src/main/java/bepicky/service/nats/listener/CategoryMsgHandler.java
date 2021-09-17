package bepicky.service.nats.listener;

import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.msg.CategoryCommandMsg;
import bepicky.common.msg.MsgCommand;
import bepicky.service.facade.functional.ICategoryFunctionalFacade;
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
public class CategoryMsgHandler {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ICategoryFunctionalFacade categoryFacade;

    @Value("${topics.category.cmd}")
    private String categoryCommandSubject;

    private Map<MsgCommand, Function<CategoryCommandMsg, CategoryResponse>> commandMapper =
        ImmutableMap.<MsgCommand, Function<CategoryCommandMsg, CategoryResponse>>builder()
        .put(MsgCommand.PICK, r -> categoryFacade.pick(r))
        .put(MsgCommand.PICK_ALL, r -> categoryFacade.pickAll(r))
        .put(MsgCommand.REMOVE, r -> categoryFacade.remove(r))
        .put(MsgCommand.REMOVE_ALL, r -> categoryFacade.removeAll(r))
        .build();


    @PostConstruct
    public void createDispatcher() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            long start = System.currentTimeMillis();
            try {
                CategoryCommandMsg cmdMsg = om.readValue(msg.getData(), CategoryCommandMsg.class);
                CategoryResponse response = commandMapper.get(cmdMsg.getCommand()).apply(cmdMsg);
                natsConnection.publish(
                    msg.getReplyTo(),
                    om.writeValueAsString(response).getBytes(StandardCharsets.UTF_8)
                );
                long total = System.currentTimeMillis() - start;
                log.info("category:{}:{}:execution_time:{}", cmdMsg.getCategoryId(), cmdMsg.getCommand(), total);
            } catch (IOException e) {
                log.error("category:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(categoryCommandSubject);
    }


}
