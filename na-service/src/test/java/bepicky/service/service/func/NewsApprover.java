package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourcePageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.junit.Assert.assertFalse;

@SpringBootTest(classes = {NAService.class, NewsApprover.NewsApproverConfiguration.class})
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("it")
@Ignore
public class NewsApprover extends FuncSupport {

    @Autowired
    private INewsService newsService;

    @Autowired
    private SourceIngestionService sourceIS;

    @Autowired
    private LanguageIngestionService languageIS;

    @Autowired
    private CategoryIngestionService categoryIS;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private ObjectMapper objectMapper;

    private FuncSourceDataIngestor dataIngestor;

    private PageContentContext pageContentContext;

    private NewsNoteContext newsContext;

    @PostConstruct
    public void setSourceData() {
        dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIS)
            .languageIS(languageIS)
            .categoryIS(categoryIS)
            .build();

        pageContentContext = new PageContentContext();
        newsContext = new NewsNoteContext(objectMapper);
    }

    @Test
    public void approveNews() {
        log.info("ingest:source:start");
        dataIngestor.ingestSources("Sources_test");
        log.info("ingest:source:finish");
        sourcePageService.findAll().forEach(this::analyseSourcePage);
    }


    private void analyseSourcePage(SourcePage sourcePage) {
        Source source = sourcePage.getSource();
        log.info("approve:sourcepage:start:{}", sourcePage.getName());
        assertFalse(sourcePage.getContentBlocks().isEmpty());

        byte[] pageContent = pageContentContext.get(source.getName().toLowerCase(), sourcePage.getName());
        String path = getPath(sourcePage);
        stub(path, pageContent);
        Set<NewsNote> freshNews = newsService.readFreshNews(sourcePage);
        stubVerify(path);

        newsContext.approve(sourcePage.getSource().getName(), sourcePage.getName(), freshNews);

        log.info("approve:sourcepage:finish:{}", sourcePage.getName());
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yml")
    @EnableTransactionManagement
    static class NewsApproverConfiguration {
    }
}
