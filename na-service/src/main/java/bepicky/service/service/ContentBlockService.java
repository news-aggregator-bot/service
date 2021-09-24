package bepicky.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.ContentBlockEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.repository.ContentBlockRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ContentBlockService implements IContentBlockService {

    @Autowired
    private ContentBlockRepository repository;

    @Override
    public List<ContentBlockEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ContentBlockEntity> findBySourcePage(SourcePageEntity page) {
        return repository.findBySourcePage(page);
    }

    @Override
    public Optional<ContentBlockEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ContentBlockEntity save(ContentBlockEntity block) {
        log.info("contentblock:save:{}", block);
        return repository.save(block);
    }

    @Override
    public List<ContentBlockEntity> saveAll(Collection<ContentBlockEntity> blocks) {
        log.info("contentblock:save:{}", blocks);
        return repository.saveAll(blocks);
    }

    @Override
    public void delete(Long id) {
        findById(id).ifPresent(b -> repository.delete(b));
    }

    @Override
    public void deleteAll(Collection<ContentBlockEntity> blocks) {
        repository.deleteAll(blocks);
    }
}
