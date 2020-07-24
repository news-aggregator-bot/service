package vlad110kg.news.aggregator.service.func.domain;

import lombok.Data;

import java.util.List;

@Data
public class FuncSource {

    private String name;
    private List<FuncSourcePage> sourcePages;
}
