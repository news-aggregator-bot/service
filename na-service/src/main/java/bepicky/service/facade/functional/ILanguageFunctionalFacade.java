package bepicky.service.facade.functional;

import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.common.msg.LanguageCommandMsg;
import bepicky.service.domain.request.ListRequest;

public interface ILanguageFunctionalFacade {

    LanguageListResponse listAll(ListRequest request);

    LanguageResponse pick(LanguageCommandMsg cmd);

    LanguageResponse remove(LanguageCommandMsg cmd);
}
