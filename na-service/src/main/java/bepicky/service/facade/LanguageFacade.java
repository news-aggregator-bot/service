package bepicky.service.facade;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.request.ListLanguageRequest;
import bepicky.common.domain.request.PickLanguageRequest;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.common.domain.response.ListLanguageResponse;
import bepicky.common.domain.response.PickLanguageResponse;
import bepicky.service.entity.Language;
import bepicky.service.entity.Reader;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LanguageFacade {

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private IReaderService readerService;

    public ListLanguageResponse listAll(ListLanguageRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:language:reader {} not found", request.getChatId());
            return new ListLanguageResponse(ErrorUtil.readerNotFound());
        }
        PageRequest pageReq = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Language> langPage = languageService.listAll(pageReq);
        List<LanguageResponse> remainingLanguages = langPage.stream()
            .filter(l -> !reader.getLanguages().contains(l))
            .map(this::toResponse)
            .collect(Collectors.toList());
        return new ListLanguageResponse(
            remainingLanguages,
            langPage.isFirst(),
            langPage.isLast(),
            reader.getPrimaryLanguage().getLang()
        );
    }

    public PickLanguageResponse pick(PickLanguageRequest request) {
        Language language = languageService.find(request.getLang()).orElse(null);
        if (language == null) {
            log.warn("pick:language:language {} not found", request.getLang());
            return new PickLanguageResponse(ErrorUtil.languageNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("pick:language:reader {} not found", request.getChatId());
            return new PickLanguageResponse(ErrorUtil.readerNotFound());
        }
        Set<Language> readerLangs = new HashSet<>();
        readerLangs.add(language);
        reader.setLanguages(readerLangs);
        readerService.save(reader);
        return new PickLanguageResponse(
            reader.getPrimaryLanguage().getLang(),
            toResponse(language)
        );
    }

    private LanguageResponse toResponse(Language language) {
        return new LanguageResponse(
            language.getLang(),
            language.getName(),
            language.getLocalized()
        );
    }
}
