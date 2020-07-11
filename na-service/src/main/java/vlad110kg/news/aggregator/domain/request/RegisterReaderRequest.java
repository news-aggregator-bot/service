package vlad110kg.news.aggregator.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterReaderRequest {

    @JsonProperty("chat_id")
    private long chatId;
    @NotBlank
    private String username;
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;
    @NotBlank
    @JsonProperty("last_name")
    private String lastName;
    @NotBlank
    private String platform;
    @NotBlank
    @JsonProperty("primary_language")
    private String primaryLanguage;
}
