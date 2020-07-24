package vlad110kg.news.aggregator.service.func;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import vlad110kg.news.aggregator.NAService;
import vlad110kg.news.aggregator.YamlPropertySourceFactory;
import vlad110kg.news.aggregator.data.ingestor.service.CategoryIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.LanguageIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.SourceIngestionService;
import vlad110kg.news.aggregator.entity.NewsNote;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.service.INewsService;
import vlad110kg.news.aggregator.service.ISourcePageService;

import javax.annotation.PostConstruct;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertFalse;

@SpringBootTest(classes = {NAService.class, NewsApprover.NewsApproverConfiguration.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@Slf4j
@Ignore
public class NewsApprover {

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

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

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
        dataIngestor.ingestSources();
        log.info("ingest:source:finish");
        sourcePageService.findAll().forEach(this::analyseSourcePage);
    }


    private void analyseSourcePage(SourcePage sourcePage) {
        Source source = sourcePage.getSource();
        log.info("approve:sourcepage:start:{}", sourcePage.getName());
        assertFalse(sourcePage.getContentBlocks().isEmpty());

        byte[] pageContent = pageContentContext.get(source.getName().toLowerCase(), sourcePage.getName());
        wireMockRule.stubFor(get(urlEqualTo(sourcePage.getUrl())).willReturn(aResponse().withBody(pageContent)));

        Set<NewsNote> freshNews = newsService.readFreshNews(sourcePage);
        newsContext.approve(sourcePage.getSource().getName(), sourcePage.getName(), freshNews);

        log.info("approve:sourcepage:finish:{}", sourcePage.getName());
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class NewsApproverConfiguration {
    }
}
