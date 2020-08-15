package bepicky.service.controller;

import bepicky.common.domain.response.SourceListResponse;
import bepicky.service.domain.request.ListRequest;
import bepicky.service.facade.functional.ISourceFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private ISourceFunctionalFacade sourceFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    public SourceListResponse list(@RequestParam Map<String, Object> params) {
        ListRequest listRequest = objectMapper.convertValue(params, ListRequest.class);
        return sourceFacade.listAll(listRequest);
    }
}
