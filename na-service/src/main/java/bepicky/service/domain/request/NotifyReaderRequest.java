package bepicky.service.domain.request;

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
