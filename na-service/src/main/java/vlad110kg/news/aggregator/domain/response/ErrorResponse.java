package vlad110kg.news.aggregator.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import vlad110kg.news.aggregator.ErrorUtil;

@Data
@Builder
public class ErrorResponse {

    private int code;

    private String entity;

    @JsonIgnore
    public boolean isReader() {
        return ErrorUtil.READER.equals(entity);
    }

    @JsonIgnore
    public boolean isReaderInactive() {
        return isReader() && isBadRequest();
    }

    @JsonIgnore
    public boolean isReaderNotFound() {
        return isReader() && isNotFound();
    }

    @JsonIgnore
    public boolean isNotFound() {
        return 404 == code;
    }

    @JsonIgnore
    public boolean isBadRequest() {
        return 400 == code;
    }
}
