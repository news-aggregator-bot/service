package bepicky.service.web.parser.doc;

import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.web.parser.JsoupEvaluatorFactory;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;
import java.util.function.Function;

@Component
public class OnlyLinkDocumentTagParser implements DocumentTagParser {

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Override
    public Optional<Tuple2<String, String>> parse(
        Element main, ContentBlock block, Function<Element, String> href
    ) {
        if (!matches(block)) {
            return Optional.empty();
        }
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        Element linkEl = main.selectFirst(evaluatorFactory.get(linkTag));
        return linkEl == null ? Optional.empty() : Optional.of(Tuples.of(linkEl.text(), href.apply(linkEl)));
    }

    @Override
    public boolean matches(ContentBlock block) {
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        return titleTag == null && linkTag != null;
    }
}
