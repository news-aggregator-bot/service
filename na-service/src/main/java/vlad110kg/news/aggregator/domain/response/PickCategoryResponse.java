package vlad110kg.news.aggregator.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickCategoryResponse {

    private CategoryResponse category;
    private String language;
    private ErrorResponse error;

    public static PickCategoryResponse error(ErrorResponse error) {
        return PickCategoryResponse.builder().error(error).build();
    }
}
