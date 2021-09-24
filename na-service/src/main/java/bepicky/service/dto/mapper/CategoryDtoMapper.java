package bepicky.service.dto.mapper;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.LocalisationEntity;
import bepicky.service.entity.LanguageEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryDtoMapper {

    public CategoryDto toFullDto(CategoryEntity c, LanguageEntity language) {
        LocalisationEntity localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setLocalised(localisation.getValue());
        categoryDto.setType(c.getType().name());
        categoryDto.setParent(toParentDto(c.getParent(), language));
        categoryDto.setChildren(c.getSubcategories()
            .stream()
            .map(subC -> toSingleDto(subC, language))
            .collect(Collectors.toList()));
        return categoryDto;
    }

    public CategoryDto toSingleDto(CategoryEntity c, LanguageEntity language) {
        if (c == null) {
            return null;
        }
        LocalisationEntity localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setType(c.getType().name());
        categoryDto.setLocalised(localisation.getValue());
        return categoryDto;
    }

    public CategoryDto toParentDto(CategoryEntity c, LanguageEntity language) {
        if (c == null) {
            return null;
        }
        LocalisationEntity localisation = getLocalisation(c, language);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(c.getId());
        categoryDto.setName(c.getName());
        categoryDto.setType(c.getType().name());
        categoryDto.setParent(toParentDto(c.getParent(), language));
        categoryDto.setLocalised(localisation.getValue());
        return categoryDto;
    }

    private LocalisationEntity getLocalisation(CategoryEntity c, LanguageEntity language) {
        return c.getLocalisations().stream()
            .filter(cl -> cl.getLanguage().equals(language))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(String.format(
                "category:localisation:404:%s:%s",
                c.getName(),
                language.getLang()
            )));
    }
}
