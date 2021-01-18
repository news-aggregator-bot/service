package bepicky.service.facade;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.dto.CategoryDto;
import bepicky.service.domain.dto.LocalisationDto;
import bepicky.service.entity.Category;
import bepicky.service.entity.Localisation;
import bepicky.service.entity.Language;
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
    public List<Category> ingest(List<CategoryDto> dtos) {
        List<Category> categories = dtos.stream()
            .map(this::getCategory)
            .collect(Collectors.toList());
        return categoryService.saveAll(categories);
    }

    private Category getCategory(CategoryDto dto) {
        Category c = categoryService.findByName(dto.getName()).orElseGet(() -> {
            Category category = new Category();
            category.setName(dto.getName());
            return category;
        });
        Category parent = categoryService.findByName(dto.getParent()).orElse(null);
        c.setParent(parent);
        c.setType(dto.getType());
        List<Localisation> localisations = convertTo(dto.getLocalisations());
        localisationService.saveAll(localisations);
        c.setLocalisations(localisations);
        return c;
    }

    public List<Localisation> convertTo(List<LocalisationDto> dtos) {
        return dtos.stream().map(cl -> {
                Language language = languageService.find(cl.getLanguage())
                    .orElseThrow(() -> new ResourceNotFoundException(cl.getLanguage() + " language not found."));
                return localisationService.findByValue(cl.getValue())
                    .stream()
                    .filter(value -> value.getLanguage().equals(language))
                    .findFirst()
                    .orElseGet(() -> {
                        Localisation localisation = new Localisation();
                        localisation.setValue(cl.getValue());
                        localisation.setLanguage(language);
                        return localisation;
                    });
            }
        ).collect(Collectors.toList());
    }
}
