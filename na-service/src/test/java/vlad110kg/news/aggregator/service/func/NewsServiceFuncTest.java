package vlad110kg.news.aggregator.service.func;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
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
import vlad110kg.news.aggregator.FuncSupport;
import vlad110kg.news.aggregator.NAService;
import vlad110kg.news.aggregator.YamlPropertySourceFactory;
import vlad110kg.news.aggregator.data.ingestor.service.CategoryIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.LanguageIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.SourceIngestionService;
import vlad110kg.news.aggregator.entity.NewsNote;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.service.INewsService;
import vlad110kg.news.aggregator.service.ISourceService;
import vlad110kg.news.aggregator.service.func.mismatch.Mismatch;
import vlad110kg.news.aggregator.service.func.mismatch.ResultMismatchAnalyzer;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@SpringBootTest(classes = {NAService.class, PageApprover.PageApproverConfiguration.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@Slf4j
@Ignore
public class NewsServiceFuncTest extends FuncSupport {

    private static final String SOURCE_MISMATCH_PATTERN = "Source: %s\n%s";
    private static final String SOURCEPAGE_MISMATCH_PATTERN = "Source page: %s\n%s";

    @Autowired
    private INewsService newsService;

    @Autowired
    private SourceIngestionService sourceIS;

    @Autowired
    private LanguageIngestionService languageIS;

    @Autowired
    private CategoryIngestionService categoryIS;

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
            .languageIS(languageIS)
            .categoryIS(categoryIS)
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
        List<Source> ingestedSources = sourceService.findAll();
        assertFalse(ingestedSources.isEmpty());

        List<Pair<Source, List<Pair<SourcePage, List<Mismatch>>>>> sourceMismatches = ingestedSources
            .stream()
            .map(this::analyseSource)
            .filter(p -> !p.getValue().isEmpty())
            .collect(Collectors.toList());
        if (!sourceMismatches.isEmpty()) {
            String sourceErrorMsg = buildErrMsg(sourceMismatches);
            fail(sourceErrorMsg);
        }
    }

    private String buildErrMsg(List<Pair<Source, List<Pair<SourcePage, List<Mismatch>>>>> sourceMismatches) {
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

    private Pair<Source, List<Pair<SourcePage, List<Mismatch>>>> analyseSource(Source source) {
        log.info("func:source:start:{}", source.getName());
        List<SourcePage> sourcePages = source.getPages();
        assertFalse(sourcePages.isEmpty());
        List<Pair<SourcePage, List<Mismatch>>> sourcePagesMismatches = source.getPages()
            .stream()
            .map(this::analyseSourcePage)
            .filter(p -> !p.getValue().isEmpty())
            .collect(Collectors.toList());
        log.info("func:source:finish:{}", source.getName());
        return Pair.create(source, sourcePagesMismatches);
    }

    private Pair<SourcePage, List<Mismatch>> analyseSourcePage(SourcePage sourcePage) {
        Source source = sourcePage.getSource();
        log.info("func:sourcepage:start:{}", sourcePage.getName());
        assertFalse(sourcePage.getContentBlocks().isEmpty());

        byte[] pageContent = pageContentContext.get(source.getName().toLowerCase(), sourcePage.getName());
        String path = getPath(sourcePage);
        stub(path, pageContent);
        Set<NewsNote> freshNews = newsService.readFreshNews(sourcePage);
        stubVerify(path);

        Set<NewsNote> expectedNotes = newsContext.get(source.getName().toLowerCase(), sourcePage.getName());

        List<Mismatch> mismatches = mismatchAnalyzer.analyse(expectedNotes, freshNews);
        log.info("func:sourcepage:finish:{}", sourcePage.getName());
        return Pair.create(sourcePage, mismatches);
    }

    @Configuration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    @EnableTransactionManagement
    static class FuncTestConfiguration {
    }

}