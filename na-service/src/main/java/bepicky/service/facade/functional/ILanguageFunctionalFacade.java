package bepicky.service.facade.functional;

import bepicky.common.domain.request.LanguageRequest;
import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.service.domain.request.ListLanguageRequest;

public interface ILanguageFunctionalFacade {

    LanguageListResponse listAll(ListLanguageRequest request);

    LanguageResponse pick(LanguageRequest request);

    LanguageResponse remove(LanguageRequest request);
}
