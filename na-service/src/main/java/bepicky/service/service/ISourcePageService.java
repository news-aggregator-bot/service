package bepicky.service.service;

import org.springframework.data.domain.Pageable;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ISourcePageService {

    Optional<SourcePageEntity> findById(Long id);

    List<SourcePageEntity> findAll();

    List<SourcePageEntity> findBySource(SourceEntity source);

    List<SourcePageEntity> findByCategory(CategoryEntity category);

    Optional<SourcePageEntity> findFirstBySource(SourceEntity source, Pageable pageable);

    Optional<SourcePageEntity> findByUrl(String url);

    SourcePageEntity save(SourcePageEntity page);

    Collection<SourcePageEntity> save(Collection<SourcePageEntity> pages);

    long countAll();

    long countBySource(SourceEntity source);

    void delete(long id);

    SourcePageEntity changeSource(SourceEntity source, long sourcePage);

    void enable(long pageId);

    void disable(long pageId);
}
