package bepicky.service.service;

import bepicky.service.entity.ContentTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IContentTagService {

    List<ContentTag> findAll();

    List<ContentTag> findByIds(Collection<Long> ids);

    List<ContentTag> findByValue(String value);

    Optional<ContentTag> findById(Long id);

    ContentTag save(ContentTag tag);

    List<ContentTag> saveAll(Collection<ContentTag> tags);

    void delete(Long id);
}
