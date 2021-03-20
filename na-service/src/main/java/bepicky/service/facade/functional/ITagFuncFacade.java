package bepicky.service.facade.functional;

import bepicky.common.domain.response.SubscribeTagResponse;

public interface ITagFuncFacade {
    SubscribeTagResponse subscribe(Long chatId, String value);
}
