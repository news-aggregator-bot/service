package bepicky.service.service;

import bepicky.service.entity.NewsNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface INewsNoteService {

    NewsNote save(NewsNote note);

    Collection<NewsNote> saveAll(Collection<NewsNote> notes);

    Optional<NewsNote> find(Long id);

    List<NewsNote> findAllByNormalisedTitle(String title);

    Optional<NewsNote> findByNormalisedTitle(String title);

    List<NewsNote> findByUrl(String url);

    Set<NewsNote> getAllAfter(Long id);

    Set<NewsNote> getNotesBetween(Date from, Date to);

    Set<NewsNote> getTodayNotes();

    Set<NewsNote> archiveEarlierThan(int months);

    boolean existsByUrl(String url);

    boolean existsByNormalisedTitle(String normalisedTitle);

    Page<NewsNote> searchByTitle(String key, Pageable pageable);

    List<NewsNote> refresh(long from, long to);

    Collection<NewsNote> flush();
}
