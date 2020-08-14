package bepicky.service.domain.mapper;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.Language;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryDtoMapper {

    public CategoryDto toFullDto(Category c, Language language) {
        CategoryLocalisation localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setLocalised(localisation.getValue());
        categoryDto.setParent(toParentResponse(c.getParent(), language));
        categoryDto.setChildren(c.getSubcategories()
            .stream()
            .map(subC -> toSingleResponse(subC, language))
            .collect(Collectors.toList()));
        return categoryDto;
    }

    public CategoryDto toSingleResponse(Category c, Language language) {
        if (c == null) {
            return null;
        }
        CategoryLocalisation localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setLocalised(localisation.getValue());
        return categoryDto;
    }

    public CategoryDto toParentResponse(Category c, Language language) {
        if (c == null) {
            return null;
        }
        CategoryLocalisation localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setParent(toParentResponse(c.getParent(), language));
        categoryDto.setLocalised(localisation.getValue());
        return categoryDto;
    }

    private CategoryLocalisation getLocalisation(Category c, Language language) {
        return c.getLocalisations().stream()
            .filter(cl -> cl.getLanguage().equals(language))
            .findFirst()
            .orElseThrow(ResourceNotFoundException::new);
    }
}
