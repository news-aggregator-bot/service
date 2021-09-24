package bepicky.service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourcePageRepository extends JpaRepository<SourcePageEntity, Long>, JpaSpecificationExecutor<SourcePageEntity> {

    List<SourcePageEntity> findAllBySource(SourceEntity source);

    List<SourcePageEntity> findAllBySource(SourceEntity source, Pageable pageable);

    List<SourcePageEntity> findAllByCategories(CategoryEntity category);

    Optional<SourcePageEntity> findByUrl(String url);

    long countBySource(SourceEntity source);
}
