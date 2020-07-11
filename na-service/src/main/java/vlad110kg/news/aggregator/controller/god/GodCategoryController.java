package vlad110kg.news.aggregator.controller.god;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad110kg.news.aggregator.domain.request.CategoryRequest;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.CategoryLocalisation;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;
import vlad110kg.news.aggregator.exception.SourceNotFoundException;
import vlad110kg.news.aggregator.service.ICategoryService;
import vlad110kg.news.aggregator.service.ILanguageService;

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
