package bepicky.service.web.parser;

import bepicky.service.domain.RawNews;
import bepicky.service.domain.RawNewsNote;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.entity.SourcePage;
import bepicky.service.web.parser.doc.DocumentTagParser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
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

    private final List<DocumentTagParser> tagParsers;
    private final JsoupEvaluatorFactory evaluatorFactory;
    private final UrlNormalisationContext urlNormalisationContext;
    private final SourcePageParserTracker spParserTracker;

    public DefaultWebContentParser(
        List<DocumentTagParser> tagParsers,
        JsoupEvaluatorFactory evaluatorFactory,
        UrlNormalisationContext urlNormalisationContext,
        SourcePageParserTracker spParserTracker
    ) {
        this.tagParsers = tagParsers;
        this.evaluatorFactory = evaluatorFactory;
        this.urlNormalisationContext = urlNormalisationContext;
        this.spParserTracker = spParserTracker;
    }

    @Override
    public RawNews parse(SourcePage page, List<String> content) {
        log.info(
            "web content parser: {}",
            page.getUrl()
        );
        for (String c : content) {
            Document doc = Parser.parse(c, page.getUrl());
            if (doc != null) {
                Set<RawNewsNote> rawNewsNotes = page.getContentBlocks()
                    .stream()
                    .map(block -> parseDoc(page, doc, block))
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                if (rawNewsNotes.size() > 1) {
                    spParserTracker.track(page.getId());
                    return new RawNews(rawNewsNotes);
                }
            }
        }
        spParserTracker.failed(page.getId());
        log.warn("web content parser:empty: {}", page.getUrl());
        return new RawNews(Collections.emptySet());
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
