package vlad110kg.news.aggregator.service.func.domain;

import lombok.Data;
import vlad110kg.news.aggregator.entity.ContentTagMatchStrategy;
import vlad110kg.news.aggregator.entity.ContentTagType;

@Data
public class FuncContentTag {

    private ContentTagType type;
    private String value;
    private ContentTagMatchStrategy matchStrategy;
}
