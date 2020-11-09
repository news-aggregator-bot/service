package bepicky.service.web.parser.doc;

import bepicky.service.entity.ContentBlock;
import org.jsoup.nodes.Element;
import reactor.util.function.Tuple2;

import java.util.Optional;
import java.util.function.Function;

public interface DocumentTagParser {

    Optional<Tuple2<String, String>> parse(Element main, ContentBlock block, Function<Element, String> href);

    boolean matches(ContentBlock block);
}
