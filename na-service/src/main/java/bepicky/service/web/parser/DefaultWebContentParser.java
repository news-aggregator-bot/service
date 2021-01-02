package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.SourcePage;
import bepicky.service.web.parser.doc.DocumentTagParser;
import bepicky.service.web.reader.WebPageReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultWebContentParser implements WebContentParser {

    @Autowired
    private List<WebPageReader> webPageReaders;

    @Autowired
    private List<DocumentTagParser> tagParsers;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Autowired
    private UrlNormalisationContext urlNormalisationContext;

    @Override
    public List<PageParsedData> parse(SourcePage page) {
        for (WebPageReader webPageReader : webPageReaders) {
            Optional<Document> doc = readDocument(page, webPageReader);
            if (doc.isPresent()) {
                List<PageParsedData> parsedData = page.getContentBlocks()
                    .stream()
                    .map(block -> parseDoc(page, doc.get(), block))
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                if (!parsedData.isEmpty()) {
                    log.info(
                        "webcontentparser:{} :{}:{}",
                        page.getUrl(),
                        webPageReader.getClass().getSimpleName(),
                        parsedData.size()
                    );
                    return parsedData;
                }
            }
        }
        log.warn("webpagereader:read:empty:{}", page.getUrl());
        return Collections.emptyList();
    }

    public Optional<Document> readDocument(SourcePage page, WebPageReader webPageReader) {
        try {
            return Optional.ofNullable(webPageReader.read(page.getUrl()));
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof org.jsoup.HttpStatusException)) {
                log.error("webpagereader:read:failed:{}:{}", page.getUrl(), e.getMessage());
            }
            return Optional.empty();
        }
    }

    private List<PageParsedData> parseDoc(SourcePage page, Document doc, ContentBlock block) {
        ContentTag mainTag = block.findByType(ContentTagType.MAIN);
        ContentTag authorTag = block.findByType(ContentTagType.AUTHOR);

        if (mainTag != null) {
            Elements mainClassElems = doc.select(evaluatorFactory.get(mainTag));

            Builder<PageParsedData> datas = ImmutableList.builder();
            for (Element main : mainClassElems) {

                PageParsedData.PageParsedDataBuilder dataBuilder = PageParsedData.builder();

                tagParsers.stream()
                    .filter(tp -> tp.matches(block))
                    .map(tp -> tp.parse(main, block, a -> getHref(page, a)))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .orElse(Optional.empty())
                    .ifPresent(tp -> {
                        dataBuilder.title(tp.getT1());
                        dataBuilder.link(tp.getT2());
                        dataBuilder.author(getAuthor(authorTag, main));
                        datas.add(dataBuilder.build());
                    });
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
