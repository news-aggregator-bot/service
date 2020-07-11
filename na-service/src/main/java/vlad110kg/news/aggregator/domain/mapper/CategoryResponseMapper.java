package vlad110kg.news.aggregator.domain.mapper;

import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.domain.response.CategoryResponse;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;

import java.util.stream.Collectors;

@Component
public class CategoryResponseMapper {

    public CategoryResponse toFullResponse(Category c, Language language) {
        CategoryLocalisation localisation = getLocalisation(c, language);
        return CategoryResponse.builder()
            .id(c.getId())
            .name(c.getName())
            .localised(localisation.getValue())
            .parent(toParentResponse(c.getParent(), language))
            .children(c.getSubcategories()
                .stream()
                .map(subC -> toSingleResponse(subC, language))
                .collect(Collectors.toList()))
            .build();
    }

    public CategoryResponse toSingleResponse(Category c, Language language) {
        if (c == null) {
            return null;
        }
        CategoryLocalisation localisation = getLocalisation(c, language);
        return CategoryResponse.builder()
            .id(c.getId())
            .name(c.getName())
            .localised(localisation.getValue())
            .build();
    }

    public CategoryResponse toParentResponse(Category c, Language language) {
        if (c == null) {
            return null;
        }
        CategoryLocalisation localisation = getLocalisation(c, language);
        return CategoryResponse.builder()
            .id(c.getId())
            .name(c.getName())
            .parent(toParentResponse(c.getParent(), language))
            .localised(localisation.getValue())
            .build();
    }

    private CategoryLocalisation getLocalisation(Category c, Language language) {
        return c.getLocalisations().stream()
            .filter(cl -> cl.getLanguage().equals(language))
            .findFirst()
            .orElseThrow(ResourceNotFoundException::new);
    }
}
