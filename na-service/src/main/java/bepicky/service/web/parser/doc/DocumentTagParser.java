package bepicky.service.web.parser.doc;

import bepicky.service.entity.ContentBlock;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface DocumentTagParser {

    Optional<Map.Entry<String, String>> parse(Element main, ContentBlock block, Function<Element, String> href);

    boolean matches(ContentBlock block);
}
