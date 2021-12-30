package bepicky.service.domain.mapper;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.NewsNoteNotification;
import org.springframework.stereotype.Component;

@Component
public class NewsNoteDtoMapper {

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
        dto.setNoteId(note.getId());
        dto.setUrl(note.getUrl());
        dto.setTitle(note.getTitle());
        dto.setAuthor(note.getAuthor());
        dto.setDate(note.getCreationDate());
        dto.setLink(NewsNoteNotificationDto.LinkDto.valueOf(notification.getLink().name()));
        dto.setLinkKey(notification.getLinkKey());
        return dto;
    }

}
