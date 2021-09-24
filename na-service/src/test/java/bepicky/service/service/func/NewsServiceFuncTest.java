package bepicky.service.service.func;

import bepicky.service.FuncSupport;
import bepicky.service.NAService;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.service.INewsService;
import bepicky.service.service.ISourceService;
import bepicky.service.service.func.mismatch.Mismatch;
import bepicky.service.service.func.mismatch.ResultMismatchAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {NAService.class, NewsServiceFuncTest.FuncTestConfiguration.class})
@ActiveProfiles("it")
@Slf4j
public class NewsServiceFuncTest extends FuncSupport {

    private static final String SOURCE_MISMATCH_PATTERN = "Source: %s\n%s";
    private static final String SOURCEPAGE_MISMATCH_PATTERN = "Source page: %s\n%s";

    @Autowired
    private INewsService newsService;

    @Autowired
    private SourceIngestionService sourceIS;

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ObjectMapper objectMapper;

    private FuncSourceDataIngestor dataIngestor;

    private PageContentContext pageContentContext;

    private NewsNoteContext newsContext;

    private ResultMismatchAnalyzer mismatchAnalyzer;

    @PostConstruct
    public void setSourceData() {
        dataIngestor = FuncSourceDataIngestor.builder()
            .sourceIS(sourceIS)
            .build();

        pageContentContext = new PageContentContext();
        newsContext = new NewsNoteContext(objectMapper);
        mismatchAnalyzer = new ResultMismatchAnalyzer();
    }

    @Test
    public void funcTest() {
        log.info("ingest:source:start");
        dataIngestor.ingestSources();
        log.info("ingest:source:finish");
        List<SourceEntity> ingestedSources = sourceService.findAll();
        assertFalse(ingestedSources.isEmpty());

        List<Pair<SourceEntity, List<Pair<SourcePageEntity, List<Mismatch>>>>> sourceMismatches = ingestedSources
            .stream()
            .map(this::analyseSource)
            .filter(p -> !p.getValue().isEmpty())
            .collect(Collectors.toList());
        if (!sourceMismatches.isEmpty()) {
            String sourceErrorMsg = buildErrMsg(sourceMismatches);
            fail(sourceErrorMsg);
        }
    }

    private String buildErrMsg(List<Pair<SourceEntity, List<Pair<SourcePageEntity, List<Mismatch>>>>> sourceMismatches) {
        return sourceMismatches.stream()
            .map(s -> {
                String sourcePageMismatchMsg = s.getValue().stream()
                    .filter(sp -> !sp.getValue().isEmpty())
                    .map(sp -> {
                        String mismatchMsg = sp.getValue()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(Mismatch::toString)
                            .collect(Collectors.joining("\n"));
                        return String.format(SOURCEPAGE_MISMATCH_PATTERN, sp.getKey().getUrl(), mismatchMsg);
                    })
                    .collect(Collectors.joining("\n-----------------NEXT-SOURCE-PAGE------------------\n"));
                return String.format(SOURCE_MISMATCH_PATTERN, s.getKey().getName(), sourcePageMismatchMsg);
            }).collect(Collectors.joining("\n-----------------NEXT-SOURCE-------------------\n"));
    }

    private Pair<SourceEntity, List<Pair<SourcePageEntity, List<Mismatch>>>> analyseSource(SourceEntity source) {
        log.info("func:source:start:{}", source.getName());
        List<SourcePageEntity> sourcePages = source.getPages();
        assertFalse(sourcePages.isEmpty());
        List<Pair<SourcePageEntity, List<Mismatch>>> sourcePagesMismatches = source.getPages()
            .stream()
            .map(this::analyseSourcePage)
            .filter(Objects::nonNull)
            .filter(p -> !p.getValue().isEmpty())
            .collect(Collectors.toList());
        log.info("func:source:finish:{}", source.getName());
        return Pair.create(source, sourcePagesMismatches);
    }

    private Pair<SourcePageEntity, List<Mismatch>> analyseSourcePage(SourcePageEntity sourcePage) {
        SourceEntity source = sourcePage.getSource();
        log.info("func:sourcepage:start:{}", sourcePage.getUrl());
        assertFalse(sourcePage.getContentBlocks().isEmpty());

        byte[] pageContent = pageContentContext.get(source.getName().toLowerCase(), sourcePage.getUrl());
        String path = getPath(sourcePage);
        stub(path, pageContent);
        Set<NewsNoteEntity> freshNews = new HashSet<>();
//            newsService.readFreshNews(sourcePage);
        stubVerify(path);

        Set<NewsNoteEntity> expectedNotes = newsContext.get(source.getName().toLowerCase(), sourcePage.getUrl());

        List<Mismatch> mismatches = mismatchAnalyzer.analyse(expectedNotes, freshNews);
        log.info("func:sourcepage:finish:{}", sourcePage.getUrl());
        return Pair.create(sourcePage, mismatches);
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class FuncTestConfiguration {

        @Bean
        @Primary
        public SourceIngestionService sourceIngestionService() {
            return new TestSourceIngestionService();
        }
    }

}