package bepicky.service.controller.god;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bepicky.service.entity.Language;
import bepicky.service.service.ILanguageService;

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
