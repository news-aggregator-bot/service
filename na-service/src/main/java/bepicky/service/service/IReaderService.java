package bepicky.service.service;

import bepicky.service.entity.Reader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IReaderService {

    Reader save(Reader reader);

    Collection<Reader> saveAll(Collection<Reader> reader);

    Optional<Reader> findById(long id);

    Optional<Reader> find(long chatId);

    List<Reader> findAll();

    List<Reader> findAllEnabled();

    Reader enable(long chatId);

    Reader disable(long chatId);

    Optional<Reader> delete(long id);
}
