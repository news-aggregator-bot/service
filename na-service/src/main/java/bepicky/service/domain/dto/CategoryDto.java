package bepicky.service.domain.dto;

import bepicky.service.entity.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDto {

    private String name;

    private String parent;

    private CategoryType type;

    private List<LocalisationDto> localisations;
}
