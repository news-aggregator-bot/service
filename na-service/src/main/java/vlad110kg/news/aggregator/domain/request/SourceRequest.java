package vlad110kg.news.aggregator.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SourceRequest {

    @NotBlank
    private String name;
}
