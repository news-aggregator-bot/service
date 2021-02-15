package bepicky.service.facade.functional;

import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;
import bepicky.service.dto.Ids;

public interface INewsNoteFunctionalFacade {

    NewsSearchResponse search(NewsSearchRequest request);

    void refresh(Ids ids);
}
