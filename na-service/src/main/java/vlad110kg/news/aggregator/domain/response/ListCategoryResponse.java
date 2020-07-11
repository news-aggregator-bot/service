package vlad110kg.news.aggregator.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListCategoryResponse {

    private List<CategoryResponse> categories;
    @JsonProperty("total_amount")
    private long totalAmount;
    private String language;
    private ErrorResponse error;

    public static ListCategoryResponse error(ErrorResponse error) {
        return ListCategoryResponse.builder().error(error).build();
    }
}
