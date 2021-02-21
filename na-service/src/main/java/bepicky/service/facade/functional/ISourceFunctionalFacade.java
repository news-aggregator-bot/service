package bepicky.service.facade.functional;

import bepicky.common.domain.dto.SourcePageDto;
import bepicky.common.domain.request.SourceRequest;
import bepicky.common.domain.response.SourceListResponse;
import bepicky.common.domain.response.SourceResponse;
import bepicky.service.domain.request.ListRequest;

public interface ISourceFunctionalFacade {

    SourceListResponse listAll(ListRequest request);

    SourceResponse pick(SourceRequest request);

    SourceResponse remove(SourceRequest request);

    SourcePageDto changeSource(long sourceId, long sourcePageId);
}
