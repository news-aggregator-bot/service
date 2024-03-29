package bepicky.service.service;

import bepicky.common.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.Category;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.repository.SourcePageRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class SourcePageService implements ISourcePageService {

    private final SourcePageRepository repository;

    @Override
    public List<SourcePage> findAll() {
        return repository.findAll();
    }

    @Override
    public List<SourcePage> findBySource(Source source) {
        return repository.findAllBySource(source);
    }

    @Override
    public List<SourcePage> findByCategory(Category category) {
        return repository.findAllByCategories(category);
    }

    @Override
    public Optional<SourcePage> findFirstBySource(Source source, Pageable pageable) {
        return repository.findAllBySource(source, pageable)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<SourcePage> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    @Override
    public SourcePage save(SourcePage page) {
        log.info("sourcepage:save:{}", page);
        return repository.save(page);
    }

    @Override
    public Collection<SourcePage> save(Collection<SourcePage> pages) {
        log.info("sourcepage:save:{}", pages);
        return repository.saveAll(pages);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public long countBySource(Source source) {
        return repository.countBySource(source);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public SourcePage changeSource(Source source, long id) {
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

    @Override
    public Optional<SourcePage> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void updateWebReader(SourcePage sp, String webReader) {
        if (sp.getWebReader() == null || !sp.getWebReader().equals(webReader)) {
            log.info(
                "sourcepage: {} :update: {} -> {}",
                sp.getUrl(),
                sp.getWebReader(),
                webReader
            );
            sp.setWebReader(webReader);
            repository.save(sp);
        }
    }
}
