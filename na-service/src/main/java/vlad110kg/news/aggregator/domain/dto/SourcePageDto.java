package vlad110kg.news.aggregator.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class SourcePageDto {

    private String name;

    private String url;

    private String language;

    private List<String> categories;

    private List<ContentBlockDto> blocks = new ArrayList<>();

    public void addBlock(ContentBlockDto block) {
        blocks.add(block);
    }
}
