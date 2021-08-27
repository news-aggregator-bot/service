package bepicky.service.web.parser.doc;

import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.web.parser.JsoupEvaluatorFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class OnlyTitleDocumentTagParser implements DocumentTagParser {

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Override
    public Optional<Map.Entry<String, String>> parse(
        Element main, ContentBlock block, Function<Element, String> href
    ) {
        if (!matches(block)) {
            return Optional.empty();
        }
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        Element titleEl = main.selectFirst(evaluatorFactory.get(titleTag));
        if (titleEl == null) {
            return Optional.empty();
        }
        Element a = getLinkEl(titleEl, main);
        if (a == null) {
            return Optional.empty();
        }
        return Optional.of(Map.entry(titleEl.text(), href.apply(a)));
    }

    @Override
    public boolean matches(ContentBlock block) {
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        return titleTag != null && linkTag == null;
    }

    private Element getLinkEl(Element titleEl, Element wrapper) {
        Evaluator.Tag linkTag = new Evaluator.Tag("a");
        Element link = titleEl.selectFirst(linkTag);
        return link == null ? wrapper.selectFirst(linkTag) : link;
    }
}
