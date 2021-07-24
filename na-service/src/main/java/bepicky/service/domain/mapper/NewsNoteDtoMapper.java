package bepicky.service.domain.mapper;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.NewsNoteNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsNoteDtoMapper {

    @Autowired
    private SourcePageDtoMapper sourcePageDtoMapper;

    public NewsNoteDto toDto(NewsNote note, Language language) {
        NewsNoteDto dto = new NewsNoteDto();
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        dto.setSourcePages(sourcePageDtoMapper.toDto(note.getSourcePages(), language));
        return dto;
    }

    public NewsNoteDto toDto(NewsNote note) {
        NewsNoteDto dto = new NewsNoteDto();
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        return dto;
    }

    public NewsNoteNotificationDto toNotificationDto(NewsNoteNotification notification) {
        NewsNoteNotificationDto dto = new NewsNoteNotificationDto();
        NewsNote note = notification.getNote();
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
