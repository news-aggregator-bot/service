package bepicky.service.domain.dto;

import lombok.Data;

@Data
public class CategoryLocalisationDto {

    private String category;

    private String value;

    private String language;
}