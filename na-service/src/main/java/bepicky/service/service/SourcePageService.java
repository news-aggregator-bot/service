package bepicky.service.service;

import bepicky.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.repository.SourcePageRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class SourcePageService implements ISourcePageService {

    private final SourcePageRepository repository;

    public SourcePageService(SourcePageRepository repository) {this.repository = repository;}

    @Override
    public Optional<SourcePageEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<SourcePageEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<SourcePageEntity> findBySource(SourceEntity source) {
        return repository.findAllBySource(source);
    }

    @Override
    public List<SourcePageEntity> findByCategory(CategoryEntity category) {
        return repository.findAllByCategories(category);
    }

    @Override
    public Optional<SourcePageEntity> findFirstBySource(SourceEntity source, Pageable pageable) {
        return repository.findAllBySource(source, pageable)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<SourcePageEntity> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    @Override
    public SourcePageEntity save(SourcePageEntity page) {
        log.info("sourcepage:save:{}", page);
        return repository.save(page);
    }

    @Override
    public Collection<SourcePageEntity> save(Collection<SourcePageEntity> pages) {
        log.info("sourcepage:save:{}", pages);
        return repository.saveAll(pages);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public long countBySource(SourceEntity source) {
        return repository.countBySource(source);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public SourcePageEntity changeSource(SourceEntity source, long id) {
        return repository.findById(id)
            .map(sp -> {
                sp.setSource(source);
                return repository.save(sp);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Source page not found."));
    }

    @Override
    public void enable(long pageId) {
        repository.findById(pageId)
            .ifPresent(sp -> {
                sp.setEnabled(true);
                repository.save(sp);
                log.info("sourcepage:enable:" + pageId);
            });
    }

    @Override
    public void disable(long pageId) {
        repository.findById(pageId)
            .ifPresent(sp -> {
                sp.setEnabled(false);
                repository.save(sp);
                log.info("sourcepage:disable:" + pageId);
            });
    }
}
