package bepicky.service.facade;

import bepicky.service.dto.Ids;
import bepicky.service.dto.LanguageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.service.ILanguageService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class IngestionLanguageFacade {

    @Autowired
    private ILanguageService languageService;

    public List<LanguageEntity> ingest(List<LanguageDto> dtos) {
        List<LanguageEntity> languages = dtos.stream().map(l -> {
            LanguageEntity language = languageService.find(l.getLang()).orElseGet(() -> {
                LanguageEntity lang = new LanguageEntity();
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
