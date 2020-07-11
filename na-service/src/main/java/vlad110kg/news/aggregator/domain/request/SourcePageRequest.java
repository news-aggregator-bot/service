package vlad110kg.news.aggregator.domain.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
public class SourcePageRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private String language;
}
