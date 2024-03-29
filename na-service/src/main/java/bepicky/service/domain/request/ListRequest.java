package bepicky.service.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListRequest {

    @JsonProperty("chat_id")
    private long chatId;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;
}
