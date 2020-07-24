package vlad110kg.news.aggregator.service.func.mismatch;

import lombok.Builder;
import lombok.Data;
import vlad110kg.news.aggregator.entity.NewsNote;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Mismatch {

    private NewsNote expected;

    private NewsNote actual;

    @Builder.Default
    private List<String> messages = new ArrayList<>();

    @Override
    public String toString() {
        return new StringBuilder("\n")
            .append("expected:").append(expected).append("\n")
            .append("actual:").append(actual).append("\n")
            .append("msgs:").append(messages).append("\n")
            .toString();
    }
}
