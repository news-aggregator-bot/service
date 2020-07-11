package vlad110kg.news.aggregator.domain.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotifyReaderRequest {

    private long chatId;

    private String lang;

    private List<NewsNoteRequest> notes;
}
