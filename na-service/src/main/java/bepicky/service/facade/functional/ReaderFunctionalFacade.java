package bepicky.service.facade.functional;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.StatusReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.entity.Language;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Tag;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
public class ReaderFunctionalFacade implements IReaderFunctionalFacade {

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryDtoMapper categoryDtoMapper;

    @Override
    public ReaderDto create(ReaderRequest request) {
        log.info("reader:registration:{}", request.toString());
        Language language = Optional.ofNullable(request.getPrimaryLanguage())
            .flatMap(languageService::find)
            .orElse(languageService.getDefault());
        Platform platform = Platform.valueOf(request.getPlatform());
        Reader reader = modelMapper.map(request, Reader.class);
        reader.setPlatform(platform);
        reader.setPrimaryLanguage(language);
        reader.setLanguages(Sets.newHashSet(language));
        return toDto(readerService.register(reader));
    }

    @Override
    public ReaderDto enable(long chatId) {
        return toDto(readerService.updateStatus(chatId, Reader.Status.ENABLED));
    }

    @Override
    public ReaderDto disable(long chatId) {
        return toDto(readerService.updateStatus(chatId, Reader.Status.DISABLED));
    }

    @Override
    public ReaderDto settings(long chatId) {
        return toDto(readerService.updateStatus(chatId, Reader.Status.IN_SETTINGS));
    }

    @Override
    public ReaderDto block(long chatId) {
        return toDto(readerService.updateStatus(chatId, Reader.Status.BLOCKED));
    }

    @Override
    public ReaderDto pause(long chatId) {
        return toDto(readerService.updateStatus(chatId, Reader.Status.PAUSED));
    }

    @Override
    @Transactional
    public ReaderDto delete(long id) {
        return readerService.delete(id).map(r -> modelMapper.map(r, ReaderDto.class)).orElse(null);
    }

    @Override
    public ReaderDto find(long chatId) {
        return readerService.findByChatId(chatId).map(this::toDto).orElse(null);
    }

    @Override
    public StatusReaderDto status(long chatId) {
        return readerService.findByChatId(chatId).map(r -> {
            Set<CategoryDto> categories = r.getCategories()
                .stream()
                .map(c -> categoryDtoMapper.toFullDto(c, r.getPrimaryLanguage()))
                .collect(Collectors.toSet());
            Set<String> tags = r.getTags()
                .stream()
                .map(Tag::getValue)
                .collect(Collectors.toSet());
            StatusReaderDto status = modelMapper.map(r, StatusReaderDto.class);
            status.setCategories(categories);
            status.setTags(tags);
            return status;
        }).orElse(null);
    }

    @Override
    public List<ReaderDto> findAll() {
        return readerService.findAll()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ReaderDto changeLanguage(long chatId, String lang) {
        return languageService.find(lang)
            .map(l -> readerService.findByChatId(chatId)
                .map(r -> {
                    r.setPrimaryLanguage(l);
                    readerService.update(r);
                    log.info("reader:primary lang:" + lang);
                    return toDto(r);
                }).orElseGet(() -> {
                    log.warn("reader:{}:404", chatId);
                    return null;
                }))
            .orElseGet(() -> {
                log.warn("reader:{}:primary lang:{}:404", chatId, lang);
                return null;
            });
    }

    private ReaderDto toDto(Reader reader) {
        return Optional.ofNullable(reader)
            .map(r -> modelMapper.map(r, ReaderDto.class))
            .orElse(null);
    }
}
