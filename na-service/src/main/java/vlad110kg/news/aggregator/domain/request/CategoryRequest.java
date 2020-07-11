package vlad110kg.news.aggregator.domain.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
public class CategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private List<CategoryLocalisation> localisations;

    @Data
    @ToString
    public static class CategoryLocalisation {

        @NotBlank
        private String value;

        @NotBlank
        private String language;
    }
}
