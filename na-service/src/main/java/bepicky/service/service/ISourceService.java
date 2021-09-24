package bepicky.service.service;

import bepicky.service.entity.SourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISourceService {

    SourceEntity create(String name);

    SourceEntity save(SourceEntity src);

    List<SourceEntity> findAll();

    List<SourceEntity> findAllEnabledByFetchPeriod(SourceEntity.FetchPeriod fetchPeriod);

    Page<SourceEntity> findAllEnabled(Pageable pageable);

    Optional<SourceEntity> find(Long id);

    Optional<SourceEntity> findByName(String name);

    Optional<SourceEntity> findById(long id);

    SourceEntity updateStatus(Long id, SourceEntity.Status status);

    SourceEntity disable(Long id);

    SourceEntity updateFetchPeriod(Long id, SourceEntity.FetchPeriod fetchPeriod);
}
