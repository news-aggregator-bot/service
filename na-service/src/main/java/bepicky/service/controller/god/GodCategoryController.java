package bepicky.service.controller.god;

import bepicky.common.domain.request.CategoryRequest;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.Language;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/god")
public class GodCategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ILanguageService languageService;

    @PostMapping("/categories")
    public Category create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.save(toCategory(request));
    }

    @PostMapping("/categories/{parentId}")
    public List<Category> create(@PathVariable Long parentId, @Valid @RequestBody List<CategoryRequest> subcategories) {

        Category parent = categoryService.find(parentId).orElseThrow(ResourceNotFoundException::new);
        List<Category> categories = subcategories.stream().map(sub -> {
            Category category = toCategory(sub);
            category.setParent(parent);
            return category;
        }).collect(Collectors.toList());
        return categoryService.saveAll(categories);
    }

    private Category toCategory(CategoryRequest request) {
        Category category = new Category();
        List<CategoryLocalisation> localisations = request.getLocalisations().stream().map(cl -> {
            CategoryLocalisation localisation = new CategoryLocalisation();
            Language language = languageService.find(cl.getLanguage()).orElseThrow(SourceNotFoundException::new);
            localisation.setCategory(category);
            localisation.setLanguage(language);
            localisation.setValue(cl.getValue());
            return localisation;
        }).collect(Collectors.toList());

        category.setName(request.getName());
        category.setLocalisations(localisations);
        return category;
    }
}
