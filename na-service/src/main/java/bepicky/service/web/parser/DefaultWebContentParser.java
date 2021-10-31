package bepicky.service.web.parser;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsNote;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.SourcePage;
import bepicky.service.web.parser.doc.DocumentTagParser;
import bepicky.service.web.reader.WebPageReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultWebContentParser implements WebContentParser {

    private final List<WebPageReader> webPageReaders;
    private final List<DocumentTagParser> tagParsers;
    private final JsoupEvaluatorFactory evaluatorFactory;
    private final UrlNormalisationContext urlNormalisationContext;
    private final SourcePageParserTracker spParserTracker;
    private Map<String, WebPageReader> webPageReaderMap;

    public DefaultWebContentParser(
        List<WebPageReader> webPageReaders,
        List<DocumentTagParser> tagParsers,
        JsoupEvaluatorFactory evaluatorFactory,
        UrlNormalisationContext urlNormalisationContext,
        SourcePageParserTracker spParserTracker
    ) {
        this.webPageReaders = webPageReaders;
        this.tagParsers = tagParsers;
        this.evaluatorFactory = evaluatorFactory;
        this.urlNormalisationContext = urlNormalisationContext;
        this.spParserTracker = spParserTracker;
    }

    @PostConstruct
    private void setWebPageReadersMap() {
        webPageReaderMap = webPageReaders.stream()
            .collect(Collectors.toMap(WebPageReader::name, Function.identity()));
    }

    @Override
    public RawNews parse(SourcePage page) {
        if (page.getWebReader() != null && webPageReaderMap.containsKey(page.getWebReader())) {
            WebPageReader spReader = webPageReaderMap.get(page.getWebReader());
            RawNews rawNewsNotes = getRawNews(page, spReader);
            if (rawNewsNotes != null) {
                return rawNewsNotes;
            }
        }
        for (WebPageReader webPageReader : webPageReaders) {
            RawNews rawNewsNotes = getRawNews(page, webPageReader);
            if (rawNewsNotes != null) {
                return rawNewsNotes;
            }
        }
        spParserTracker.failed(page.getId());
        log.warn("webpageparser:read:empty:{}", page.getUrl());
        return new RawNews(Collections.emptySet(), null);
    }

    private RawNews getRawNews(SourcePage page, WebPageReader webPageReader) {
        log.info(
            "webcontentparser:{} :{}",
            page.getUrl(),
            webPageReader.getClass().getSimpleName()
        );
        Optional<Document> doc = readDocument(page, webPageReader);
        if (doc.isPresent()) {
            Set<RawNewsNote> rawNewsNotes = page.getContentBlocks()
                .stream()
                .map(block -> parseDoc(page, doc.get(), block))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            if (rawNewsNotes.size() > 1) {
                spParserTracker.track(page.getId());
                return new RawNews(rawNewsNotes, webPageReader.name());
            }
        }
        return null;
    }

    public Optional<Document> readDocument(SourcePage page, WebPageReader webPageReader) {
        try {
            return Optional.ofNullable(webPageReader.read(page.getUrl()));
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof org.jsoup.HttpStatusException)) {
                log.error("webpagereader:read:failed: {} : {}", page.getUrl(), e.getMessage());
            }
            return Optional.empty();
        }
    }

    private List<RawNewsNote> parseDoc(SourcePage page, Document doc, ContentBlock block) {
        ContentTag mainTag = block.findByType(ContentTagType.MAIN);
        ContentTag authorTag = block.findByType(ContentTagType.AUTHOR);

        if (mainTag != null) {
            Elements mainClassElems = doc.select(evaluatorFactory.get(mainTag));

            Builder<RawNewsNote> datas = ImmutableList.builder();
            for (Element main : mainClassElems) {


                tagParsers.stream()
                    .filter(tp -> tp.matches(block))
                    .map(tp -> tp.parse(main, block, a -> getHref(page, a)))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .orElse(Optional.empty())
                    .ifPresent(tp -> datas.add(new RawNewsNote(
                        tp.getKey(),
                        tp.getValue(),
                        getAuthor(authorTag, main)
                    )));
            }
            return datas.build();
        }
        return Collections.emptyList();
    }

    private String getHref(SourcePage page, Element a) {
        return urlNormalisationContext.normaliseUrl(page, a);
    }

    private String getAuthor(ContentTag authorTag, Element wrapper) {
        if (authorTag != null) {
            Element element = wrapper.selectFirst(evaluatorFactory.get(authorTag));
            return element == null ? null : element.text();
        }
        return null;
    }

}
