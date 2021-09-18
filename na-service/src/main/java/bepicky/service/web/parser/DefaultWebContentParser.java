package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.SourcePage;
import bepicky.service.nats.publisher.AdminMessagePublisher;
import bepicky.service.web.parser.doc.DocumentTagParser;
import bepicky.service.web.reader.WebPageReader;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultWebContentParser implements WebContentParser {

    @Autowired
    private List<WebPageReader> webPageReaders;

    @Autowired
    private List<DocumentTagParser> tagParsers;

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Autowired
    private UrlNormalisationContext urlNormalisationContext;

    @Autowired
    private AdminMessagePublisher adminPublisher;

    @Override
    public Set<PageParsedData> parse(SourcePage page) {
        for (WebPageReader webPageReader : webPageReaders) {
//            log.info(
//                "webcontentparser:{} :{}",
//                page.getUrl(),
//                webPageReader.getClass().getSimpleName()
//            );
            Optional<Document> doc = readDocument(page, webPageReader);
            if (doc.isPresent()) {
                Set<PageParsedData> parsedData = page.getContentBlocks()
                    .stream()
                    .map(block -> parseDoc(page, doc.get(), block))
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                if (parsedData.size() > 1) {
                    return parsedData;
                }
            }
        }
        log.warn("webpagereader:read:empty:{}", page.getUrl());
        adminPublisher.publish("empty page " + page.getUrl());
        return Collections.emptySet();
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


                tagParsers.stream()
                    .filter(tp -> tp.matches(block))
                    .map(tp -> tp.parse(main, block, a -> getHref(page, a)))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .orElse(Optional.empty())
                    .ifPresent(tp -> datas.add(new PageParsedData(
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
