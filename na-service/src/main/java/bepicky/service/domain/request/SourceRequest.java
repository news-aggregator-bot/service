package bepicky.service.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SourceRequest {

    @NotBlank
    private String name;
}
