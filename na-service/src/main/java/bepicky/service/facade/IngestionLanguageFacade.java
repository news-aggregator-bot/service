package bepicky.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.domain.dto.LanguageDto;
import bepicky.service.entity.Language;
import bepicky.service.service.ILanguageService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class IngestionLanguageFacade {

    @Autowired
    private ILanguageService languageService;

    public List<Language> ingest(List<LanguageDto> dtos) {
        List<Language> languages = dtos.stream().map(l -> {
            Language language = languageService.find(l.getLang()).orElseGet(() -> {
                Language lang = new Language();
                lang.setLang(l.getLang());
                return lang;
            });
            language.setName(l.getName());
            language.setLocalized(l.getLocalised());
            return language;
        }).collect(Collectors.toList());
        return languageService.saveAll(languages);
    }
}
