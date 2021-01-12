package bepicky.service.domain.mapper;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsNoteDtoMapper {

    @Autowired
    private SourcePageDtoMapper sourcePageDtoMapper;

    public NewsNoteDto toDto(NewsNote note, Language language) {
        NewsNoteDto request = new NewsNoteDto();
        request.setUrl(note.getUrl());
        request.setTitle(note.getTitle());
        request.setAuthor(note.getAuthor());
        request.setDate(note.getCreationDate());
        request.setSourcePage(sourcePageDtoMapper.toDto(note.getSourcePage(), language));
        return request;
    }

}
