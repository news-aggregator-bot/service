package bepicky.service.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponse {

    private long id;
    private String name;
    private String localised;
    private CategoryResponse parent;
    private List<CategoryResponse> children;
}
