package bepicky.service.facade.functional;

import bepicky.common.domain.response.TagListResponse;
import bepicky.common.domain.response.TagResponse;

public interface ITagFuncFacade {
    TagResponse subscribe(Long chatId, String value);

    TagResponse unsubscribe(Long chatId, String value);

    TagListResponse search(String value);

    TagListResponse getAll(Long chatId);
}
