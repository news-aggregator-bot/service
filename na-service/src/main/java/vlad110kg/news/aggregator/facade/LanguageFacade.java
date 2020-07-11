package vlad110kg.news.aggregator.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.ErrorUtil;
import vlad110kg.news.aggregator.domain.request.ListLanguageRequest;
import vlad110kg.news.aggregator.domain.request.PickLanguageRequest;
import vlad110kg.news.aggregator.domain.response.LanguageResponse;
import vlad110kg.news.aggregator.domain.response.ListLanguageResponse;
import vlad110kg.news.aggregator.domain.response.PickLanguageResponse;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.entity.Reader;
import vlad110kg.news.aggregator.service.ILanguageService;
import vlad110kg.news.aggregator.service.IReaderService;

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
            return ListLanguageResponse.error(ErrorUtil.readerNotFound());
        }
        PageRequest pageReq = PageRequest.of(request.getPage() - 1, request.getSize());
        List<Language> languages = languageService.listAll(pageReq);
        long totalAmount = languageService.countAll();
        return ListLanguageResponse.builder()
            .languages(languages.stream().map(LanguageResponse::new).collect(Collectors.toList()))
            .totalAmount(totalAmount)
            .language(reader.getPrimaryLanguage().getLang())
            .build();
    }

    public PickLanguageResponse pick(PickLanguageRequest request) {
        Language language = languageService.find(request.getLang()).orElse(null);
        if (language == null) {
            log.warn("pick:language:language {} not found", request.getLang());
            return PickLanguageResponse.error(ErrorUtil.languageNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("pick:language:reader {} not found", request.getChatId());
            return PickLanguageResponse.error(ErrorUtil.readerNotFound());
        }
        Set<Language> readerLangs = new HashSet<>();
        readerLangs.add(language);
        reader.setLanguages(readerLangs);
        readerService.save(reader);
        return PickLanguageResponse.builder()
            .language(new LanguageResponse(language))
            .lang(reader.getPrimaryLanguage().getLang())
            .build();
    }

}
