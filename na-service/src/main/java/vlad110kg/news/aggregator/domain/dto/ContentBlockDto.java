package vlad110kg.news.aggregator.domain.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContentBlockDto {

    private final List<ContentTagDto> contentTags = new ArrayList<>();

    public void add(ContentTagDto tag) {
        contentTags.add(tag);
    }
}
