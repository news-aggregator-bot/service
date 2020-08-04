package bepicky.service.facade;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.dto.CategoryLocalisationDto;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.Language;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class IngestionCategoryLocalisationFacade {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ILanguageService languageService;

    public List<CategoryLocalisation> ingest(List<CategoryLocalisationDto> dtos) {
        List<CategoryLocalisation> categories = dtos.stream().map(cl -> {
                Category category = categoryService.findByName(cl.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException(cl.getCategory() + " category not found."));
                Language language = languageService.find(cl.getLanguage())
                    .orElseThrow(() -> new ResourceNotFoundException(cl.getLanguage() + " language not found."));
                return categoryService.findLocalisationByValue(cl.getValue())
                    .stream()
                    .filter(value -> value.getCategory().equals(category) && value.getLanguage().equals(language))
                    .findFirst()
                    .orElseGet(() -> {
                        CategoryLocalisation localisation = new CategoryLocalisation();
                        localisation.setValue(cl.getValue());
                        localisation.setCategory(category);
                        localisation.setLanguage(language);
                        return localisation;
                    });
            }
        ).collect(Collectors.toList());
        return categoryService.saveAllLocalisations(categories);
    }
}
