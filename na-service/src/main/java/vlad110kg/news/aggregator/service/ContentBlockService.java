package vlad110kg.news.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.entity.ContentBlock;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.repository.ContentBlockRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContentBlockService implements IContentBlockService {

    @Autowired
    private ContentBlockRepository repository;

    @Override
    public List<ContentBlock> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ContentBlock> findBySourcePage(SourcePage page) {
        return repository.findBySourcePage(page);
    }

    @Override
    public Optional<ContentBlock> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ContentBlock save(ContentBlock block) {
        log.info("contentblock:save:{}", block);
        return repository.save(block);
    }

    @Override
    public List<ContentBlock> saveAll(List<ContentBlock> blocks) {
        log.info("contentblock:save:{}", blocks);
        return repository.saveAll(blocks);
    }

    @Override
    public void delete(Long id) {
        findById(id).ifPresent(b -> repository.delete(b));
    }

    @Override
    public void deleteAll(Collection<ContentBlock> blocks) {
        repository.deleteAll(blocks);
    }
}
