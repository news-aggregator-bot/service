package bepicky.service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.Category;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourcePageRepository extends JpaRepository<SourcePage, Long>, JpaSpecificationExecutor<SourcePage> {

    List<SourcePage> findAllBySource(Source source);

    List<SourcePage> findAllBySource(Source source, Pageable pageable);

    List<SourcePage> findAllByCategories(Category category);

    Optional<SourcePage> findByUrl(String url);

    long countBySource(Source source);
}
