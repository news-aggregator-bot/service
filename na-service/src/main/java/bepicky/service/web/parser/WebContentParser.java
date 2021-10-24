package bepicky.service.web.parser;

import bepicky.service.domain.RawNews;
import bepicky.service.entity.SourcePage;

public interface WebContentParser {

    RawNews parse(SourcePage page);
}
