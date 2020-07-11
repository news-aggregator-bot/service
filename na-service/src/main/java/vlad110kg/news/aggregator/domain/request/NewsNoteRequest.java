package vlad110kg.news.aggregator.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class NewsNoteRequest {

    private String title;

    private String url;

    private String description;

    private String author;

    @JsonProperty("source_page")
    private SourcePageRequest sourcePage;
}
