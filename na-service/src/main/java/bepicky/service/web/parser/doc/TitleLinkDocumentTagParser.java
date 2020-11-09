package bepicky.service.web.parser.doc;

import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagType;
import bepicky.service.web.parser.JsoupEvaluatorFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;
import java.util.function.Function;

@Component
public class TitleLinkDocumentTagParser implements DocumentTagParser {

    @Autowired
    private JsoupEvaluatorFactory evaluatorFactory;

    @Override
    public Optional<Tuple2<String, String>> parse(
        Element main, ContentBlock block, Function<Element, String> href
    ) {
        if (!matches(block)) {
            return Optional.empty();
        }
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        Element titleEl = main.selectFirst(evaluatorFactory.get(titleTag));
        if (titleEl == null) {
            return Optional.empty();
        }
        Element linkEl = main.selectFirst(evaluatorFactory.get(linkTag));
        if (linkEl == null) {
            return Optional.empty();
        }
        String link = href.apply(linkEl);
        if (StringUtils.isBlank(link)) {
            return Optional.empty();
        }
        return Optional.of(Tuples.of(titleEl.text(), link));
    }

    @Override
    public boolean matches(ContentBlock block) {
        ContentTag titleTag = block.findByType(ContentTagType.TITLE);
        ContentTag linkTag = block.findByType(ContentTagType.LINK);
        return titleTag != null && linkTag != null;
    }
}
