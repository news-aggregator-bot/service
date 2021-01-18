package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {NAService.class})
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("it")
public class CategoriesLocalisationFuncTest extends FuncSupport {

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
            Assert.fail(errMsg);
        }
    }


    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class CategoriesLocalisationFuncTestConfiguration {
    }
}
