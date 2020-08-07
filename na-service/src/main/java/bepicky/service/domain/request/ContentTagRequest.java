package bepicky.common.domain.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
public class ContentTagRequest {

    @NotBlank
    private String main;

    @NotBlank
    private String title;

    private String description;
}
