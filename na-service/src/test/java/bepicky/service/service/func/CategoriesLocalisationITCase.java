package bepicky.service.service.func;

import picky.test.SingletonMySQLContainerSupport;
import picky.test.NatsContainerSupport;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
@Testcontainers
public class CategoriesLocalisationITCase
    implements SingletonMySQLContainerSupport, NatsContainerSupport {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ILanguageService languageService;

    @Test
    @Transactional
    public void getAllCategoriesAndLocalisations_EachCategoryShouldHaveAllLanguagesTranslations() {
        List<Language> languages = languageService.getAll();
        List<Category> notAllLocalisationCategories = categoryService.getAll().stream()
            .filter(c -> c.getLocalisations().size() != languages.size())
            .collect(Collectors.toList());

        if (!notAllLocalisationCategories.isEmpty()) {
            String errMsg = notAllLocalisationCategories.stream().map(c ->
                String.format(
                    "%s contains %d localisations but must contain %d",
                    c.getName(),
                    c.getLocalisations().size(),
                    languages.size()
                )
            ).collect(Collectors.joining("\n"));
            fail(errMsg);
        }
    }
}
