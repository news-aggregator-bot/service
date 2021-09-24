package bepicky.service.service;

import bepicky.service.entity.ContentBlockEntity;
import bepicky.service.entity.SourcePageEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IContentBlockService {

    List<ContentBlockEntity> findAll();

    List<ContentBlockEntity> findBySourcePage(SourcePageEntity page);

    Optional<ContentBlockEntity> findById(Long id);

    ContentBlockEntity save(ContentBlockEntity block);

    List<ContentBlockEntity> saveAll(Collection<ContentBlockEntity> blocks);

    void delete(Long id);

    void deleteAll(Collection<ContentBlockEntity> blocks);
}
