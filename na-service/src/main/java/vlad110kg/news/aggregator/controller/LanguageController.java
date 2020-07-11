package vlad110kg.news.aggregator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vlad110kg.news.aggregator.domain.request.ListLanguageRequest;
import vlad110kg.news.aggregator.domain.request.PickLanguageRequest;
import vlad110kg.news.aggregator.domain.response.ListLanguageResponse;
import vlad110kg.news.aggregator.domain.response.PickLanguageResponse;
import vlad110kg.news.aggregator.facade.LanguageFacade;

import java.util.Map;

@RestController
public class LanguageController {

    @Autowired
    private LanguageFacade languageFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/language/list")
    public ListLanguageResponse findAll(@RequestParam Map<String, Object> params) {
        ListLanguageRequest request = objectMapper.convertValue(params, ListLanguageRequest.class);
        return languageFacade.listAll(request);
    }

    @PostMapping("/language/pick")
    public PickLanguageResponse pick(@RequestBody PickLanguageRequest request) {
        return languageFacade.pick(request);
    }
}
