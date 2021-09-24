package bepicky.service.dto.mapper;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.NewsNoteNotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsNoteDtoMapper {

    @Autowired
    private SourcePageDtoMapper sourcePageDtoMapper;

    public NewsNoteDto toDto(NewsNoteEntity note, LanguageEntity language) {
        NewsNoteDto dto = new NewsNoteDto();
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        dto.setSourcePages(sourcePageDtoMapper.toDto(note.getSourcePages(), language));
        return dto;
    }

    public NewsNoteDto toDto(NewsNoteEntity note) {
        NewsNoteDto dto = new NewsNoteDto();
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        return dto;
    }

    public NewsNoteNotificationDto toNotificationDto(NewsNoteNotificationEntity notification) {
        NewsNoteNotificationDto dto = new NewsNoteNotificationDto();
        NewsNoteEntity note = notification.getNote();
        dto.setNoteId(note.getId());
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        dto.setSourcePages(sourcePageDtoMapper.toDto(note.getSourcePages(), notification.getReader().getPrimaryLanguage()));
        dto.setLink(NewsNoteNotificationDto.LinkDto.valueOf(notification.getLink().name()));
        dto.setLinkKey(notification.getLinkKey());
        return dto;
    }

}
