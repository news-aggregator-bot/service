package vlad110kg.news.aggregator.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickLanguageResponse {

    private LanguageResponse language;
    private String lang;
    private ErrorResponse error;

    public static PickLanguageResponse error(ErrorResponse error) {
        return PickLanguageResponse.builder().error(error).build();
    }
}
