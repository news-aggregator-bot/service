package bepicky.service.service;

import bepicky.service.entity.NewsNote;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface INewsNoteService {

    NewsNote save(NewsNote note);

    Collection<NewsNote> saveAll(Collection<NewsNote> notes);

    Optional<NewsNote> find(Long id);

    Optional<NewsNote> findByUrl(String url);

    Set<NewsNote> getAllAfter(Long id);

    Set<NewsNote> getNotesBetween(Date from, Date to);

    Set<NewsNote> getTodayNotes();

    boolean exists(String url);
}
