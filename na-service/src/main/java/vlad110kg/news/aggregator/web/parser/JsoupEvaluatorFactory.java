package vlad110kg.news.aggregator.web.parser;

import com.google.common.collect.ImmutableMap;
import org.jsoup.select.Evaluator;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.entity.ContentTag;
import vlad110kg.news.aggregator.entity.ContentTagMatchStrategy;

import java.util.Map;
import java.util.function.Function;

@Component
public class JsoupEvaluatorFactory {

    private final Map<ContentTagMatchStrategy, Function<String, Evaluator>> container =
        ImmutableMap.<ContentTagMatchStrategy, Function<String, Evaluator>>builder()
            .put(ContentTagMatchStrategy.EQUALS, Evaluator.Class::new)
            .put(ContentTagMatchStrategy.STARTS, v -> new Evaluator.AttributeWithValueContaining("class", v + " "))
            .build();


    public Evaluator get(ContentTag tag) {
        return container.get(tag.getMatchStrategy()).apply(tag.getValue());
    }
}
