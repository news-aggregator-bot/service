package bepicky.service.service;

import bepicky.service.entity.NewsNoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface INewsNoteService {

    NewsNoteEntity save(NewsNoteEntity note);

    Collection<NewsNoteEntity> saveAll(Collection<NewsNoteEntity> notes);

    Optional<NewsNoteEntity> find(Long id);

    List<NewsNoteEntity> findAllByNormalisedTitle(String title);

    Optional<NewsNoteEntity> findByNormalisedTitle(String title);

    Set<NewsNoteEntity> getAllAfter(Long id);

    Set<NewsNoteEntity> getNotesBetween(Date from, Date to);

    Set<NewsNoteEntity> getTodayNotes();

    Set<NewsNoteEntity> archiveEarlierThan(int months);

    boolean existsByUrl(String url);

    boolean existsByNormalisedTitle(String normalisedTitle);

    Page<NewsNoteEntity> searchByTitle(String key, Pageable pageable);

    List<NewsNoteEntity> refresh(long from, long to);
}
