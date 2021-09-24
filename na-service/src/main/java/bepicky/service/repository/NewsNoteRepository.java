package bepicky.service.repository;

import bepicky.service.entity.NewsNoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsNoteRepository extends JpaRepository<NewsNoteEntity, Long> {

    List<NewsNoteEntity> findAllByNormalisedTitle(String normalisedTitle);

    Optional<NewsNoteEntity> findTopByNormalisedTitleOrderByIdDesc(String normalisedTitle);

    boolean existsByUrl(String url);

    boolean existsByNormalisedTitle(String title);

    Set<NewsNoteEntity> findByIdGreaterThan(Long id);

    Set<NewsNoteEntity> findByCreationDateBetween(Date startDate, Date endDate);

    Set<NewsNoteEntity> findByCreationDateBefore(Date limit);

    Page<NewsNoteEntity> findByNormalisedTitleContainsOrderByCreationDateDesc(String key, Pageable pageable);

    Page<NewsNoteEntity> findByNormalisedTitleInOrderByCreationDateDesc(Collection<String> keys, Pageable pageable);

    List<NewsNoteEntity> findAllByIdIn(Collection<Long> ids);
}
