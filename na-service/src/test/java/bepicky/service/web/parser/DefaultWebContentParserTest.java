package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.SourcePage;
import bepicky.service.web.reader.WebPageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringJUnitConfig
@TestInstance(PER_CLASS)
@Disabled
public class DefaultWebContentParserTest {

    @Autowired
    private WebContentParser webContentParser;

    @MockBean
    private WebPageReader webPageReader;

    @Autowired
    private ObjectMapper om;

    private static Stream<Arguments> data() {
        return Stream.of(
            Arguments.of("lowcost_news")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void parse_ProvidedHtmlPag_WithProvidedSourcePage_ShouldReturnExpectedData(String name)
        throws JsonProcessingException {
        SourcePage sp = readSourcePage(name);
        Set<PageParsedData> expected = readExpected(name);

        Document doc = toDoc(readPage(name), sp.getUrl());

        Mockito.when(webPageReader.read(sp.getUrl())).thenReturn(doc);

        Set<PageParsedData> parsed = webContentParser.parse(sp);

        Assertions.assertEquals(expected, parsed);
    }

    private Document toDoc(String content, String path) {
        return Parser.parse(content, path);
    }

    private SourcePage readSourcePage(String sp) throws JsonProcessingException {
        return om.readValue(readData("source-page/" + sp + ".json"), SourcePage.class);
    }

    private Set<PageParsedData> readExpected(String name) throws JsonProcessingException {
        return om.readValue(
            readData("expected/" + name + ".json"),
            om.getTypeFactory().constructCollectionType(Set.class, PageParsedData.class)
        );
    }

    private String readPage(String page) {
        return readData("data/" + page);
    }

    private String readData(String page) {
        try {
            return IOUtils.toString(getClass().getResource("/content/parser/" + page));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Configuration
    @ComponentScan("bepicky.service.web.parser.doc")
    static class DefaultWebContentParserTestConfiguration {

        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return om;
        }

        @Bean
        public JsoupEvaluatorFactory evaluatorFactory() {
            return new JsoupEvaluatorFactory();
        }

        @Bean
        public UrlNormalisationContext urlNormalisationContext() {
            return new UrlNormalisationContext();
        }

    }
}