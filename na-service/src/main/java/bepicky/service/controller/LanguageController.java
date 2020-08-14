package bepicky.service.controller;

import bepicky.common.domain.request.LanguageRequest;
import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.service.domain.request.ListLanguageRequest;
import bepicky.service.facade.functional.ILanguageFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        ListLanguageRequest request = objectMapper.convertValue(params, ListLanguageRequest.class);
        return languageFacade.listAll(request);
    }

    @PostMapping("/language/pick")
    public LanguageResponse pick(@RequestBody LanguageRequest request) {
        return languageFacade.pick(request);
    }

    @PostMapping("/language/remove")
    public LanguageResponse remove(@RequestBody LanguageRequest request) {
        return languageFacade.remove(request);
    }
}
