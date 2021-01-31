package bepicky.service.web.parser;

import bepicky.service.domain.PageParsedData;
import bepicky.service.entity.SourcePage;

import java.util.List;
import java.util.Set;

public interface WebContentParser {

    Set<PageParsedData> parse(SourcePage page);
}
