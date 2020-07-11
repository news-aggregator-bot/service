package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.entity.ContentTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IContentTagService {

    List<ContentTag> findAll();

    List<ContentTag> findByIds(Collection<Long> ids);

    List<ContentTag> findByValue(String value);

    Optional<ContentTag> findById(Long id);

    ContentTag save(ContentTag tag);

    List<ContentTag> saveAll(List<ContentTag> tags);

    void delete(Long id);
}
