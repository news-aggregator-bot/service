package bepicky.service.controller;

import bepicky.common.domain.response.LanguageListResponse;
import bepicky.service.domain.request.ListRequest;
import bepicky.service.facade.functional.ILanguageFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LanguageController {

    @Autowired
    private ILanguageFunctionalFacade languageFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/language/list")
    public LanguageListResponse findAll(@RequestParam Map<String, Object> params) {
        ListRequest request = objectMapper.convertValue(params, ListRequest.class);
        return languageFacade.listAll(request);
    }
}
