package bepicky.service.facade.functional;

import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;

public interface INewsNoteFunctionalFacade {

    NewsSearchResponse search(NewsSearchRequest request);

}
