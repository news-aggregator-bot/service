package bepicky.service.service;

import bepicky.service.entity.NewsNote;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.Reader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface INewsNoteNotificationService {

    NewsNoteNotification saveSingleNew(Reader reader, NewsNote note, NewsNoteNotification.Link link, String key);

    List<NewsNoteNotification> saveNew(Reader reader, Collection<NewsNote> notes);

    List<NewsNoteNotification> findAllNew(Reader reader);

    Optional<NewsNoteNotification> find(Reader reader, NewsNote note);

    boolean exists(Reader reader, NewsNote note);

    NewsNoteNotification sent(Long chatId, Long noteId);

    void archiveOld();

}
