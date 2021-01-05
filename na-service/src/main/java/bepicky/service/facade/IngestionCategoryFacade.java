package bepicky.service.facade;

import bepicky.service.domain.dto.CategoryDto;
import bepicky.service.entity.Category;
import bepicky.service.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionCategoryFacade {

    @Autowired
    private ICategoryService categoryService;

    @Transactional
    public List<Category> ingest(List<CategoryDto> dtos) {
        List<Category> categories = dtos.stream()
            .map(this::getCategory)
            .map(categoryService::save)
            .collect(Collectors.toList());
        return categoryService.saveAll(categories);
    }

    private Category getCategory(CategoryDto c) {
        Category cat = categoryService.findByName(c.getName()).orElseGet(() -> {
            Category category = new Category();
            category.setName(c.getName());
            return category;
        });
        Category parent = categoryService.findByName(c.getParent()).orElse(null);
        cat.setParent(parent);
        cat.setType(c.getType());
        return cat;
    }
}
