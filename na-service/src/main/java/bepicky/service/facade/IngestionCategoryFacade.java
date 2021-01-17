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
        return c;
    }
}
