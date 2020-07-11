package vlad110kg.news.aggregator.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.domain.dto.CategoryDto;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.service.ICategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionCategoryFacade {

    @Autowired
    private ICategoryService categoryService;

    public List<Category> ingest(List<CategoryDto> dtos) {
        return dtos.stream()
            .map(this::getCategory)
            .map(categoryService::save)
            .collect(Collectors.toList());
    }

    private Category getCategory(CategoryDto c) {
        Category cat = categoryService.findByName(c.getName()).orElseGet(() -> {
            Category category = new Category();
            category.setName(c.getName());
            return category;
        });
        Category parent = categoryService.findByName(c.getParent()).orElse(null);
        cat.setParent(parent);
        return cat;
    }
}
