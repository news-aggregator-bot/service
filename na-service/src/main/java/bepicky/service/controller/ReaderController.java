package bepicky.service.controller;

import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.ReaderRequest;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.Language;
import bepicky.service.entity.Platform;
import bepicky.service.entity.Reader;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/reader")
public class ReaderController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ILanguageService languageService;

    @PostMapping("/register")
    public ReaderDto register(@Valid @RequestBody ReaderRequest dto) {
        Language language = languageService.find(dto.getPrimaryLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getPrimaryLanguage() + " language not found."));
        Platform platform = Platform.valueOf(dto.getPlatform());
        Reader reader = modelMapper.map(dto, Reader.class);
        reader.setPlatform(platform);
        reader.setPrimaryLanguage(language);
        reader.setLanguages(Sets.newHashSet(language));
        reader.setStatus(Reader.Status.DISABLED);
        return modelMapper.map(readerService.save(reader), ReaderDto.class);
    }

    @PutMapping("/enable/{chatId}")
    public ReaderDto enable(@PathVariable long chatId) {
        return modelMapper.map(readerService.enable(chatId), ReaderDto.class);
    }

    @PutMapping("/disable/{chatId}")
    public ReaderDto disable(@PathVariable long chatId) {
        return modelMapper.map(readerService.disable(chatId), ReaderDto.class);
    }

    @GetMapping("/{chatId}")
    public ReaderDto find(@PathVariable long chatId) {
        return readerService.find(chatId).map(r -> modelMapper.map(r, ReaderDto.class)).orElse(null);
    }
}
