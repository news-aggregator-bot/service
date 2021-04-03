package bepicky.service.controller;

import bepicky.common.domain.request.SubscribeTagRequest;
import bepicky.common.domain.request.UnsubscribeTagRequest;
import bepicky.common.domain.response.TagResponse;
import bepicky.service.facade.functional.ITagFuncFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private ITagFuncFacade tagFacade;

    @PostMapping("/subscribe")
    public TagResponse subscribe(@Valid @RequestBody SubscribeTagRequest tagRequest) {
        return tagFacade.subscribe(tagRequest.getChatId(), tagRequest.getValue());
    }

    @PostMapping("/unsubscribe")
    public TagResponse unsubscribe(@Valid @RequestBody UnsubscribeTagRequest tagRequest) {
        return tagFacade.unsubscribe(tagRequest.getChatId(), tagRequest.getTagId());
    }
}
