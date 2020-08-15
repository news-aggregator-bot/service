package bepicky.service.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListCategoryRequest extends ListRequest {

    @JsonProperty("parent_id")
    private long parentId;
}
