package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.SourcePage;

import java.util.List;

public interface WebContentParser {

    List<PageParsedData> parse(SourcePage page, ContentBlock tag);
}
