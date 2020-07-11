package vlad110kg.news.aggregator.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageParsedData {

    private final String title;

    private final String link;

    private final String description;

    private final String author;

}
