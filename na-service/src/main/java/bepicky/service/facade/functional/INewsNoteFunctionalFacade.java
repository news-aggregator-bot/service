package bepicky.service.facade.functional;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;
import bepicky.service.dto.Ids;

import java.util.List;

public interface INewsNoteFunctionalFacade {

    NewsSearchResponse search(NewsSearchRequest request);

    List<NewsNoteDto> refresh(Ids ids);
}
