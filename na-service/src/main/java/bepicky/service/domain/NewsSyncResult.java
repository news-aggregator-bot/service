package bepicky.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import bepicky.service.entity.NewsNote;

import java.util.Set;

@Builder
@Getter
public class NewsSyncResult {

    @JsonProperty("news_notes")
    private final Set<NewsNote> newsNotes;
}
