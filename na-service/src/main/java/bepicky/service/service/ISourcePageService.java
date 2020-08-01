package bepicky.service.service;

import org.springframework.data.domain.Pageable;
import bepicky.service.entity.Category;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;

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
