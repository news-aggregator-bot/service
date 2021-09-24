package bepicky.service.service;

import bepicky.service.entity.ContentTagEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IContentTagService {

    List<ContentTagEntity> findAll();

    List<ContentTagEntity> findByIds(Collection<Long> ids);

    List<ContentTagEntity> findByValue(String value);

    Optional<ContentTagEntity> findById(Long id);

    ContentTagEntity save(ContentTagEntity tag);

    List<ContentTagEntity> saveAll(Collection<ContentTagEntity> tags);

    void delete(Long id);
}
