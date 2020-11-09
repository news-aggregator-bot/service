package bepicky.service.web.parser;

import bepicky.service.entity.ContentTag;
import bepicky.service.entity.ContentTagMatchStrategy;
import com.google.common.collect.ImmutableMap;
import org.jsoup.select.Evaluator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class JsoupEvaluatorFactory {

    private final Map<ContentTagMatchStrategy, Function<String, Evaluator>> container =
        ImmutableMap.<ContentTagMatchStrategy, Function<String, Evaluator>>builder()
            .put(ContentTagMatchStrategy.HTML_TAG, Evaluator.Tag::new)
            .put(ContentTagMatchStrategy.EQUALS, Evaluator.Class::new)
            .put(ContentTagMatchStrategy.STARTS, v -> new Evaluator.AttributeWithValueContaining("class", v + " "))
            .build();


    public Evaluator get(ContentTag tag) {
        return container.get(tag.getMatchStrategy()).apply(tag.getValue());
    }
}
