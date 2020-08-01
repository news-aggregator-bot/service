package bepicky.service.service;

import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.SourcePage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IContentBlockService {

    List<ContentBlock> findAll();

    List<ContentBlock> findBySourcePage(SourcePage page);

    Optional<ContentBlock> findById(Long id);

    ContentBlock save(ContentBlock block);

    List<ContentBlock> saveAll(List<ContentBlock> blocks);

    void delete(Long id);

    void deleteAll(Collection<ContentBlock> blocks);
}
