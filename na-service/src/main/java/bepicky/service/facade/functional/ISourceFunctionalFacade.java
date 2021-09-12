package bepicky.service.facade.functional;

import bepicky.common.domain.response.SourceListResponse;
import bepicky.common.domain.response.SourceResponse;
import bepicky.common.msg.SourceCommandMsg;
import bepicky.service.domain.request.ListRequest;

public interface ISourceFunctionalFacade {

    SourceListResponse listAll(ListRequest request);

    SourceResponse pick(SourceCommandMsg msg);

    SourceResponse remove(SourceCommandMsg msg);

    void changeSource(long sourceId, long sourcePageId);
}
