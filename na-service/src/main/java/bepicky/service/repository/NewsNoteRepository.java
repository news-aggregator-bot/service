package bepicky.service.repository;

import bepicky.service.entity.NewsNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsNoteRepository extends JpaRepository<NewsNote, Long> {

    Optional<NewsNote> findByNormalisedTitle(String normalisedTitle);

    boolean existsByUrl(String url);

    boolean existsByNormalisedTitle(String title);

    Set<NewsNote> findByIdGreaterThan(Long id);

    Set<NewsNote> findByCreationDateBetween(Date startDate, Date endDate);

    Page<NewsNote> findByNormalisedTitleContainsOrderByCreationDateDesc(String key, Pageable pageable);
}
