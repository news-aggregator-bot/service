package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.LanguageDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.response.LanguageListResponse;
import bepicky.common.domain.response.LanguageResponse;
import bepicky.common.msg.LanguageCommandMsg;
import bepicky.service.domain.request.ListRequest;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.entity.ReaderEntity;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
public class LanguageFunctionalFacade implements ILanguageFunctionalFacade {

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LanguageListResponse listAll(ListRequest request) {
        ReaderEntity reader = readerService.findByChatId(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:language:reader {} not found", request.getChatId());
            return new LanguageListResponse(ErrorUtil.readerNotFound());
        }
        PageRequest pageReq = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<LanguageEntity> langPage = languageService.listAll(pageReq);
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
    public LanguageResponse pick(LanguageCommandMsg msg) {
        return handleLangAction(msg, ReaderEntity::addLanguage);
    }

    @Override
    public LanguageResponse remove(LanguageCommandMsg request) {
        return handleLangAction(request, ReaderEntity::removeLanguage);
    }

    private LanguageResponse handleLangAction(LanguageCommandMsg request, BiConsumer<ReaderEntity, LanguageEntity> langAction) {
        LanguageEntity language = languageService.find(request.getLang()).orElse(null);
        if (language == null) {
            log.warn("update:language:language {} not found", request.getLang());
            return new LanguageResponse(ErrorUtil.languageNotFound());
        }
        ReaderEntity reader = readerService.findByChatId(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("update:language:reader {} not found", request.getChatId());
            return new LanguageResponse(ErrorUtil.readerNotFound());
        }
        langAction.accept(reader, language);
        readerService.update(reader);
        return new LanguageResponse(
            modelMapper.map(reader, ReaderDto.class),
            modelMapper.map(language, LanguageDto.class)
        );
    }

}
