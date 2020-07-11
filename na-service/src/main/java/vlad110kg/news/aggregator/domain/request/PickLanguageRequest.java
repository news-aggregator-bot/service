package vlad110kg.news.aggregator.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PickLanguageRequest {

    @JsonProperty("chat_id")
    private long chatId;

    private String lang;
}
