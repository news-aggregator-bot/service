package vlad110kg.news.aggregator.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vlad110kg.news.aggregator.domain.request.RegisterReaderRequest;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.entity.Platform;
import vlad110kg.news.aggregator.entity.Reader;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;
import vlad110kg.news.aggregator.service.ILanguageService;
import vlad110kg.news.aggregator.service.IReaderService;

import javax.validation.Valid;

@RestController
public class ReaderController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ILanguageService languageService;

    @PostMapping("/reader/register")
    public Reader register(@Valid @RequestBody RegisterReaderRequest dto) {
        Language language = languageService.find(dto.getPrimaryLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getPrimaryLanguage() + " language not found."));
        Platform platform = Platform.valueOf(dto.getPlatform());
        Reader reader = modelMapper.map(dto, Reader.class);
        reader.setPlatform(platform);
        reader.setPrimaryLanguage(language);
        return readerService.save(reader);
    }

    @PutMapping("/reader/{chatId}/enable")
    public boolean enable(@PathVariable long chatId) {
        return readerService.enable(chatId);
    }

    @PutMapping("/reader/{chatId}/disable")
    public boolean disable(@PathVariable long chatId) {
        return readerService.disable(chatId);
    }
}
