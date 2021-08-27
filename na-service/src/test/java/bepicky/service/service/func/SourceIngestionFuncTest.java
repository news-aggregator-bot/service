package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@SpringBootTest(classes = {NAService.class})
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
