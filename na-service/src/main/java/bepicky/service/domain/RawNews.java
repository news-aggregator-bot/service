package bepicky.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawNews {
    private Set<RawNewsArticle> articles;

    private String url;

    @JsonProperty("web_reader")
    private String webReader;
}
