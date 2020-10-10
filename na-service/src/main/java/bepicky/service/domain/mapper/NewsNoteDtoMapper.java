package bepicky.service.domain.mapper;

import bepicky.common.domain.request.NewsNoteRequest;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsNoteDtoMapper {

    @Autowired
    private SourcePageDtoMapper sourcePageDtoMapper;

    public NewsNoteRequest toDto(NewsNote note, Language language) {
        NewsNoteRequest request = new NewsNoteRequest();
        request.setUrl(note.getUrl());
        request.setTitle(note.getTitle());
        request.setAuthor(note.getAuthor());
        request.setSourcePage(sourcePageDtoMapper.toDto(note.getSourcePage(), language));
        return request;
    }
}
