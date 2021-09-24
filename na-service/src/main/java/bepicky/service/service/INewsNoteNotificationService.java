package bepicky.service.service;

import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.NewsNoteNotificationEntity;
import bepicky.service.entity.ReaderEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface INewsNoteNotificationService {

    NewsNoteNotificationEntity saveSingleNew(ReaderEntity reader, NewsNoteEntity note, NewsNoteNotificationEntity.Link link, String key);

    List<NewsNoteNotificationEntity> saveNew(ReaderEntity reader, Collection<NewsNoteEntity> notes);

    List<NewsNoteNotificationEntity> findAllNew(ReaderEntity reader);

    Optional<NewsNoteNotificationEntity> find(ReaderEntity reader, NewsNoteEntity note);

    boolean exists(ReaderEntity reader, NewsNoteEntity note);

    NewsNoteNotificationEntity sent(Long chatId, Long noteId);

    void archiveOld();

}
