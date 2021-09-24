package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourcePageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(classes = {NAService.class, NewsApprover.NewsApproverConfiguration.class})
@Slf4j
@ActiveProfiles("it")
//@Ignore
public class NewsApprover extends FuncSupport {

    @Autowired
    private INewsService newsService;

    @Autowired
    private SourceIngestionService sourceIS;

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
            .build();

        pageContentContext = new PageContentContext();
        newsContext = new NewsNoteContext(objectMapper);
    }

    @Test
    public void approveNews() {
        log.info("ingest:source:start");
        dataIngestor.ingestSources();
        log.info("ingest:source:finish");
        sourcePageService.findAll().forEach(this::analyseSourcePage);
    }


    private void analyseSourcePage(SourcePageEntity sourcePage) {
        SourceEntity source = sourcePage.getSource();
        log.info("approve:sourcepage:start:{}", sourcePage.getUrl());
        assertFalse(sourcePage.getContentBlocks().isEmpty());

        byte[] pageContent = pageContentContext.get(source.getName().toLowerCase(), sourcePage.getUrl());
        String path = getPath(sourcePage);
        stub(path, pageContent);
        Set<NewsNoteEntity> freshNews = new HashSet<>();
//            newsService.readFreshNews(sourcePage);
        stubVerify(path);
        if (freshNews.size() <= 1) {
            throw new IllegalStateException("Single note on the whole page? " + sourcePage.getUrl());
        }

        newsContext.approve(sourcePage.getSource().getName(), sourcePage.getUrl(), freshNews);

        log.info("approve:sourcepage:finish:{}", sourcePage.getUrl());
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class NewsApproverConfiguration {
        @Bean
        @Primary
        public SourceIngestionService sourceIngestionService() {
            return new TestSourceIngestionService();
        }
    }
}
