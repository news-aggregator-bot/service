package bepicky.service.domain.mapper;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.LanguageDto;
import bepicky.common.domain.dto.SourcePageDto;
import bepicky.service.entity.Language;
import bepicky.service.entity.SourcePage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SourcePageDtoMapper {

    @Autowired
    private CategoryDtoMapper categoryDtoMapper;

    @Autowired
    private ModelMapper modelMapper;

    public List<SourcePageDto> toDto(Collection<SourcePage> sourcePages, Language language) {
        return sourcePages.stream().map(d -> toDto(d, language)).collect(Collectors.toList());
    }

    public SourcePageDto toDto(SourcePage sourcePage, Language language) {
        SourcePageDto request = new SourcePageDto();
        request.setName(sourcePage.getName());
        request.setUrl(sourcePage.getUrl());
        request.setSourceName(sourcePage.getSource().getName());
        List<CategoryDto> categoryDtos = sourcePage.getCategories().stream()
            .map(c -> categoryDtoMapper.toSingleDto(c, language))
            .collect(Collectors.toList());
        request.setCategories(categoryDtos);
        List<LanguageDto> languageDtos = sourcePage.getLanguages().stream()
            .map(l -> modelMapper.map(l, LanguageDto.class))
            .collect(Collectors.toList());
        request.setLanguages(languageDtos);
        return request;
    }

    public SourcePageDto toDto(SourcePage sourcePage) {
        SourcePageDto request = new SourcePageDto();
        request.setName(sourcePage.getName());
        request.setUrl(sourcePage.getUrl());
        request.setSourceName(sourcePage.getSource().getName());
        List<LanguageDto> languageDtos = sourcePage.getLanguages().stream()
            .map(l -> modelMapper.map(l, LanguageDto.class))
            .collect(Collectors.toList());
        request.setLanguages(languageDtos);
        return request;
    }
}
