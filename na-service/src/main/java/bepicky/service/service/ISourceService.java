package bepicky.service.service;

import bepicky.service.entity.Source;
import bepicky.service.entity.SourceStatus;

import java.util.List;
import java.util.Optional;

public interface ISourceService {

    Source create(String name);

    Source save(Source src);

    List<Source> findAll();

    List<Source> findAllEnabled();

    Optional<Source> find(Long id);

    Optional<Source> findByName(String name);

    Source updateStatus(Long id, SourceStatus status);

    Source disable(Long id);
}
