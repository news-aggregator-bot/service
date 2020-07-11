package vlad110kg.news.aggregator.web.parser;

import vlad110kg.news.aggregator.domain.PageParsedData;
import vlad110kg.news.aggregator.entity.ContentBlock;
import vlad110kg.news.aggregator.entity.SourcePage;

import java.util.List;

public interface WebContentParser {

    List<PageParsedData> parse(SourcePage page, ContentBlock tag);
}
