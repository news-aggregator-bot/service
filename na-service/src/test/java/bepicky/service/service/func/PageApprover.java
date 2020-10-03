package bepicky.service.service.func;

import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.configuration.WebPageReaderConfiguration;
import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.SourcePage;
import bepicky.service.exception.SourceException;
import bepicky.service.service.ISourcePageService;
import bepicky.service.web.reader.JsoupWebPageReader;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Path;

@SpringBootTest(classes = {NAService.class, PageApprover.PageApproverConfiguration.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@Slf4j
@Ignore
public class PageApprover {

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private SourceIngestionService sourceIS;

    @Autowired
    private LanguageIngestionService languageIS;

    @Autowired
    private CategoryIngestionService categoryIS;

    private FuncSourceDataIngestor dataIngestor;

    private PageContentContext pageContentContext;

    private final JsoupWebPageReader webPageReader = new JsoupWebPageReader();

    @Before
    public void setUpData() {
        dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIS)
            .languageIS(languageIS)
            .categoryIS(categoryIS)
            .build();

        pageContentContext = new PageContentContext();
    }


    @Test
    public void approvePages() {
        log.info("ingest:source:start");
        dataIngestor.ingestSources("Sources_approve");
        log.info("ingest:source:finish");

        sourcePageService.findAll()
            .parallelStream()
            .filter(s -> !pageContentContext.exists(s.getSource().getName(), s.getName()))
            .forEach(sourcePage -> {
                log.info("read:sourcepage:start:{}", sourcePage.getName());
                Document pageData = readDocument(sourcePage);
                if (pageData == null) {
                    return;
                }
                log.info("read:sourcepage:complete:{}", sourcePage.getName());
                log.info("sync:sourcepage:start");
                Path syncResult = pageContentContext.approve(
                    sourcePage.getSource().getName(),
                    sourcePage.getName(),
                    pageData.html()
                );
                log.info("sync:sourcepage:complete:{}", syncResult.getFileName());
            });
    }

    private Document readDocument(SourcePage sourcePage) {
        try {
            return webPageReader.read(sourcePage.getUrl());
        } catch (SourceException e) {
            log.error("read:sourcepage:failed:{}", sourcePage.getUrl());
            return null;
        }
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    @Import(WebPageReaderConfiguration.class)
    static class PageApproverConfiguration {
    }

}
