package bepicky.service.service;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.SourceEntity;
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
    public SourceEntity create(String name) {
        SourceEntity src = new SourceEntity();
        src.setName(name);
        return save(src);
    }

    @Override
    public SourceEntity save(SourceEntity src) {
        log.info("source:save:{}", src);
        return repository.save(src);
    }

    @Override
    public List<SourceEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<SourceEntity> findAllEnabledByFetchPeriod(SourceEntity.FetchPeriod fetchPeriod) {
        return repository.findByStatusNotAndFetchPeriod(SourceEntity.Status.DISABLED, fetchPeriod);
    }

    @Override
    public Page<SourceEntity> findAllEnabled(Pageable pageable) {
        return repository.findByStatusOrderByNameAsc(SourceEntity.Status.PRIMARY, pageable);
    }

    @Override
    public Optional<SourceEntity> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<SourceEntity> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<SourceEntity> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public SourceEntity updateStatus(Long id, SourceEntity.Status status) {
        return internalUpdateStatus(id, status);
    }

    @Override
    public SourceEntity disable(Long id) {
        return internalUpdateStatus(id, SourceEntity.Status.DISABLED);
    }

    @Override
    public SourceEntity updateFetchPeriod(Long id, SourceEntity.FetchPeriod fetchPeriod) {
        return find(id).map(s -> {
            s.setFetchPeriod(fetchPeriod);
            repository.save(s);
            log.info("source:{}:fetch period update {}", s.getName(), fetchPeriod);
            return s;
        }).orElseThrow(() -> new ResourceNotFoundException("Source " + id + " not found."));
    }

    private SourceEntity internalUpdateStatus(Long id, SourceEntity.Status status) {
        return find(id).map(s -> {
            s.setStatus(status);
            repository.save(s);
            log.info("source:{}:status update {}", s.getName(), status);
            return s;
        }).orElseThrow(() -> new ResourceNotFoundException("Source " + id + " not found."));
    }

}
