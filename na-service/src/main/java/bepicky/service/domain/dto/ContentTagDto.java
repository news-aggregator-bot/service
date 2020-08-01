package bepicky.service.domain.dto;

import lombok.Data;
import lombok.ToString;
import bepicky.service.entity.ContentTagMatchStrategy;
import bepicky.service.entity.ContentTagType;

@Data
@ToString
public class ContentTagDto {

    private final ContentTagType type;
    private String value;
    private ContentTagMatchStrategy matchStrategy;
}
