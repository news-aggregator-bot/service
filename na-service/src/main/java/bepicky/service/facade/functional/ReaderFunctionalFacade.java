package bepicky.service.facade.functional;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.dto.StatusReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.entity.Language;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
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
        Language language = languageService.find(request.getPrimaryLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(request.getPrimaryLanguage() + " language not found."));
        Platform platform = Platform.valueOf(request.getPlatform());
        Reader reader = modelMapper.map(request, Reader.class);
        reader.setPlatform(platform);
        reader.setPrimaryLanguage(language);
        reader.setLanguages(Sets.newHashSet(language));
        reader.setStatus(Reader.Status.DISABLED);
        return modelMapper.map(readerService.save(reader), ReaderDto.class);
    }

    @Override
    public ReaderDto enable(long chatId) {
        return modelMapper.map(readerService.enable(chatId), ReaderDto.class);
    }

    @Override
    public ReaderDto disable(long chatId) {
        return modelMapper.map(readerService.disable(chatId), ReaderDto.class);
    }

    @Override
    public ReaderDto pause(long chatId) {
        return null;
    }

    @Override
    @Transactional
    public ReaderDto delete(long id) {
        return readerService.delete(id).map(r -> modelMapper.map(r, ReaderDto.class)).orElse(null);
    }

    @Override
    public ReaderDto find(long chatId) {
        return readerService.find(chatId).map(r -> modelMapper.map(r, ReaderDto.class)).orElse(null);
    }

    @Override
    public StatusReaderDto status(long chatId) {
        return readerService.find(chatId).map(r -> {
            Set<CategoryDto> categories = r.getCategories()
                .stream()
                .map(c -> categoryDtoMapper.toFullDto(c, r.getPrimaryLanguage()))
                .collect(
                    Collectors.toSet());
            StatusReaderDto status = modelMapper.map(r, StatusReaderDto.class);
            status.setCategories(categories);
            return status;
        }).orElse(null);
    }

    @Override
    public List<ReaderDto> findAll() {
        return readerService.findAll()
            .stream()
            .map(r -> modelMapper.map(r, ReaderDto.class))
            .collect(Collectors.toList());
    }
}
