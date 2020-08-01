package bepicky.service.service.func.domain;

import lombok.Data;
import bepicky.service.entity.ContentTagMatchStrategy;
import bepicky.service.entity.ContentTagType;

@Data
public class FuncContentTag {

    private ContentTagType type;
    private String value;
    private ContentTagMatchStrategy matchStrategy;
}
