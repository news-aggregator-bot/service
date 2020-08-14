package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.LanguageDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.LanguageRequest;
import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.service.domain.request.ListLanguageRequest;
import bepicky.service.entity.Language;
import bepicky.service.entity.Reader;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LanguageFunctionalFacade implements ILanguageFunctionalFacade {

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LanguageListResponse listAll(ListLanguageRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:language:reader {} not found", request.getChatId());
            return new LanguageListResponse(ErrorUtil.readerNotFound());
        }
        PageRequest pageReq = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Language> langPage = languageService.listAll(pageReq);
        List<LanguageDto> languages = langPage.stream()
            .map(l -> modelMapper.map(l, LanguageDto.class))
            .collect(Collectors.toList());
        return new LanguageListResponse(
            languages,
            langPage.isFirst(),
            langPage.isLast(),
            modelMapper.map(reader, ReaderDto.class)
        );
    }

    @Override
    public LanguageResponse pick(LanguageRequest request) {
        return handleLangAction(request, Reader::addLanguage);
    }

    @Override
    public LanguageResponse remove(LanguageRequest request) {
        return handleLangAction(request, Reader::removeLanguage);
    }

    private LanguageResponse handleLangAction(LanguageRequest request, BiConsumer<Reader, Language> langAction) {
        Language language = languageService.find(request.getLang()).orElse(null);
        if (language == null) {
            log.warn("update:language:language {} not found", request.getLang());
            return new LanguageResponse(ErrorUtil.languageNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("update:language:reader {} not found", request.getChatId());
            return new LanguageResponse(ErrorUtil.readerNotFound());
        }
        langAction.accept(reader, language);
        readerService.save(reader);
        return new LanguageResponse(
            modelMapper.map(reader, ReaderDto.class),
            modelMapper.map(language, LanguageDto.class)
        );
    }

}
