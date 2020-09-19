package bepicky.service.domain.dto;

import bepicky.service.entity.CategoryType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {

    private String name;

    private String parent;

    private CategoryType type;
}
