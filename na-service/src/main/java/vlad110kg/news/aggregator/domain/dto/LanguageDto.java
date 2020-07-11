package vlad110kg.news.aggregator.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LanguageDto {

    private String lang;
    private String name;
    private String localised;
}
