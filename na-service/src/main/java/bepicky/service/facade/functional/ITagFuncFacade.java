package bepicky.service.facade.functional;

import bepicky.common.domain.response.TagListResponse;
import bepicky.common.domain.response.TagResponse;

public interface ITagFuncFacade {
    TagResponse subscribe(Long chatId, String value);

    TagResponse unsubscribe(Long chatId, Long tagId);

    TagListResponse search(String value);
}
