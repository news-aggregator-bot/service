package bepicky.service.service;

import bepicky.service.entity.Source;

import java.util.List;
import java.util.Optional;

public interface ISourceService {

    Source create(String name);

    Source save(Source name);

    List<Source> findAll();

    List<Source> findAllActive();

    Optional<Source> find(Long id);

    Optional<Source> findByName(String name);

    Source enable(Long id);

    Source disable(Long id);
}
