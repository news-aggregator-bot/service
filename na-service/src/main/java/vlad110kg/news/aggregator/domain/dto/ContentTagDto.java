package vlad110kg.news.aggregator.domain.dto;

import lombok.Data;
import lombok.ToString;
import vlad110kg.news.aggregator.entity.ContentTagType;

@Data
@ToString
public class ContentTagDto {

    private final ContentTagType type;
    private String value;
}
