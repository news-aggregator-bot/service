package vlad110kg.news.aggregator.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListLanguageResponse {

    private List<LanguageResponse> languages;
    @JsonProperty("total_amount")
    private long totalAmount;
    private String language;
    private ErrorResponse error;

    public static ListLanguageResponse error(ErrorResponse error) {
        return ListLanguageResponse.builder().error(error).build();
    }
}
