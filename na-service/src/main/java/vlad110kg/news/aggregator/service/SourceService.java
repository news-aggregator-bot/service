package vlad110kg.news.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.repository.SourceRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SourceService implements ISourceService {

    @Autowired
    private SourceRepository repository;

    @Override
    public Source create(String name) {
        Source src = new Source();
        src.setName(name);
        return save(src);
    }

    @Override
    public Source save(Source src) {
        log.info("source:save:{}", src);
        return repository.save(src);
    }

    @Override
    public List<Source> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Source> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Source> findByName(String name) {
        return repository.findByName(name);
    }
}
