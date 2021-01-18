package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ISourceService;
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
public class SourceIngestionFuncTest extends FuncSupport {

    @Autowired
    private SourceIngestionService sourceIS;

    private FuncSourceDataIngestor dataIngestor;

    @PostConstruct
    public void ingest() {
        dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIS)
            .build();
    }

    @Test
    public void getAllCategoriesAndLocalisations_EachCategoryShouldHaveAllLanguagesTranslations() {
        dataIngestor.ingestSources();
    }


    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class SourceIngestionFuncTestConfiguration {
    }
}
