package vlad110kg.news.aggregator.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class SourceDto {

    private String name;
    private List<SourcePageDto> pages = new ArrayList<>();

    public void addPage(SourcePageDto page) {
        pages.add(page);
    }
}
