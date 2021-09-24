package bepicky.service.facade;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.dto.CategoryDto;
import bepicky.service.dto.Ids;
import bepicky.service.dto.LocalisationDto;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.LocalisationEntity;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.ILocalisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionCategoryFacade {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ILocalisationService localisationService;

    @Transactional
    public List<CategoryEntity> ingest(List<CategoryDto> dtos) {
        List<CategoryEntity> categories = dtos.stream()
            .map(this::getCategory)
            .collect(Collectors.toList());
        return categoryService.saveAll(categories);
    }

    private CategoryEntity getCategory(CategoryDto dto) {
        CategoryEntity c = categoryService.findByName(dto.getName()).orElseGet(() -> {
            CategoryEntity category = new CategoryEntity();
            category.setName(dto.getName());
            return category;
        });
        CategoryEntity parent = categoryService.findByName(dto.getParent()).orElse(null);
        c.setParent(parent);
        c.setType(dto.getType());
        List<LocalisationEntity> localisations = convertTo(dto.getLocalisations());
        localisationService.saveAll(localisations);
        c.setLocalisations(localisations);
        return c;
    }

    public List<LocalisationEntity> convertTo(List<LocalisationDto> dtos) {
        return dtos.stream().map(cl -> {
                LanguageEntity language = languageService.find(cl.getLanguage())
                    .orElseThrow(() -> new ResourceNotFoundException(cl.getLanguage() + " language not found."));
                return localisationService.findByValue(cl.getValue())
                    .stream()
                    .filter(value -> value.getLanguage().equals(language))
                    .findFirst()
                    .orElseGet(() -> {
                        LocalisationEntity localisation = new LocalisationEntity();
                        localisation.setValue(cl.getValue());
                        localisation.setLanguage(language);
                        return localisation;
                    });
            }
        ).collect(Collectors.toList());
    }
}
