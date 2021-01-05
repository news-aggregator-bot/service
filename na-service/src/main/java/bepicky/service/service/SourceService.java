package bepicky.service.service;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.Source;
import bepicky.service.repository.SourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
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
    public List<Source> findAllEnabled() {
        return repository.findByStatusNot(Source.Status.DISABLED);
    }

    @Override
    public Page<Source> findAllEnabled(Pageable pageable) {
        return repository.findByStatusNotOrderByNameAsc(Source.Status.DISABLED, pageable);
    }

    @Override
    public Optional<Source> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Source> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Source updateStatus(Long id, Source.Status status) {
        return update(id, status, "source:{}:enabled");
    }

    @Override
    public Source disable(Long id) {
        return update(id, Source.Status.DISABLED, "source:{}:disabled");
    }

    private Source update(Long id, Source.Status status, String logMessage) {
        return find(id).map(s -> {
            s.setStatus(status);
            repository.save(s);
            log.info(logMessage, s.getName());
            return s;
        }).orElseThrow(() -> new ResourceNotFoundException("Source " + id + " not found."));
    }
}
