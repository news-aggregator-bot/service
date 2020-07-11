package vlad110kg.news.aggregator.controller.god;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.service.ILanguageService;

import javax.validation.Valid;

@RestController
@RequestMapping("/god")
public class GodLanguageController {

    @Autowired
    private ILanguageService languageService;

    @PostMapping("/language")
    public void create(@Valid @RequestBody Language language) {
        languageService.save(language);
    }
}
