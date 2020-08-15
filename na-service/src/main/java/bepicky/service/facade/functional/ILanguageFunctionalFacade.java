package bepicky.service.facade.functional;

import bepicky.common.domain.request.LanguageRequest;
import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.service.domain.request.ListRequest;

public interface ILanguageFunctionalFacade {

    LanguageListResponse listAll(ListRequest request);

    LanguageResponse pick(LanguageRequest request);

    LanguageResponse remove(LanguageRequest request);
}
