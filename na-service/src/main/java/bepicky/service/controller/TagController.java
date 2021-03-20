package bepicky.service.controller;

import bepicky.common.domain.request.SubscribeTagRequest;
import bepicky.common.domain.response.SubscribeTagResponse;
import bepicky.service.facade.functional.ITagFuncFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("/tags")
@Slf4j
public class TagController {

    @Autowired
    private ITagFuncFacade tagFacade;

    @PostMapping("/subscribe")
    public SubscribeTagResponse create(@Valid @RequestBody SubscribeTagRequest tagRequest) {
        return tagFacade.subscribe(tagRequest.getChatId(), tagRequest.getValue());
    }
}
