package vlad110kg.news.aggregator.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;

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
