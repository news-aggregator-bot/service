package bepicky.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.domain.dto.CategoryDto;
import bepicky.service.entity.Category;
import bepicky.service.service.ICategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngestionCategoryFacade {

    @Autowired
    private ICategoryService categoryService;

    @Transactional
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
