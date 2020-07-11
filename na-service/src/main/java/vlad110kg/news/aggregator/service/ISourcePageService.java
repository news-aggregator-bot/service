package vlad110kg.news.aggregator.service;

import org.springframework.data.domain.Pageable;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ISourcePageService {

    List<SourcePage> findAll();

    List<SourcePage> findBySource(Source source);

    List<SourcePage> findByCategory(Category category);

    Optional<SourcePage> findFirstBySource(Source source, Pageable pageable);

    Optional<SourcePage> findByUrl(String url);

    SourcePage save(SourcePage page);

    Collection<SourcePage> save(Collection<SourcePage> pages);

    long countAll();

    long countBySource(Source source);

    void delete(long id);
}
