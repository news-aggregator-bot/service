package bepicky.service.service;

import bepicky.service.entity.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISourceService {

    Source create(String name);

    Source save(Source src);

    List<Source> findAll();

    List<Source> findAllEnabled();

    Page<Source> findAllEnabled(Pageable pageable);

    Optional<Source> find(Long id);

    Optional<Source> findByName(String name);

    Optional<Source> findById(long id);

    Source updateStatus(Long id, Source.Status status);

    Source disable(Long id);
}
