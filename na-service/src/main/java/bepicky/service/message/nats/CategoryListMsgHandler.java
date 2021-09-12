package bepicky.service.message.nats;

import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.msg.CategoryListMsg;
import bepicky.common.msg.ListCommand;
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
public class CategoryListMsgHandler {

    @Autowired
    private Connection natsConnection;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ICategoryFunctionalFacade categoryFacade;

    @Value("${topics.category.list}")
    private String categoryListSubject;

    private Map<ListCommand, Function<CategoryListMsg, CategoryListResponse>> listMapper =
        ImmutableMap.<ListCommand, Function<CategoryListMsg, CategoryListResponse>>builder()
        .put(ListCommand.LIST, r -> categoryFacade.listAll(r))
        .put(ListCommand.SUBLIST, r -> categoryFacade.sublist(r))
        .put(ListCommand.LIST_APPLICABLE, r -> categoryFacade.listApplicable(r))
        .build();


    @PostConstruct
    public void manageSubscription() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            long start = System.currentTimeMillis();
            try {
                CategoryListMsg listMsg = om.readValue(msg.getData(), CategoryListMsg.class);
                CategoryListResponse response = listMapper.get(listMsg.getCommand()).apply(listMsg);
                natsConnection.publish(
                    msg.getReplyTo(),
                    om.writeValueAsString(response).getBytes(StandardCharsets.UTF_8)
                );
                long total = System.currentTimeMillis() - start;
                log.info("category:{}:{}:execution_time:{}", listMsg.getId(), listMsg.getCommand(), total);
            } catch (IOException e) {
                log.error("category:failed: {}", msg.getData(), e);
            }
        });
        dispatcher.subscribe(categoryListSubject);
    }


}
