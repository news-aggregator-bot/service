package bepicky.service.domain.request;

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
