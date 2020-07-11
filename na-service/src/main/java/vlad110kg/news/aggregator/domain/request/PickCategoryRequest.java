package vlad110kg.news.aggregator.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PickCategoryRequest {

    @JsonProperty("chat_id")
    private long chatId;

    @JsonProperty("category_id")
    private long categoryId;
}
