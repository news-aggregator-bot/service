package vlad110kg.news.aggregator.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.domain.dto.CategoryLocalisationDto;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;
import vlad110kg.news.aggregator.service.ICategoryService;
import vlad110kg.news.aggregator.service.ILanguageService;

import java.util.List;
import java.util.stream.Collectors;

@Component
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
