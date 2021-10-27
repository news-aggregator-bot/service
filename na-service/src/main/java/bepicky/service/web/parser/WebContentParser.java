package bepicky.service.web.parser;

import bepicky.service.domain.RawNews;
import bepicky.service.entity.SourcePage;

import java.util.List;

public interface WebContentParser {

    RawNews parse(SourcePage page, List<String> content);
}
