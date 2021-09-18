package bepicky.service.service;

import bepicky.service.entity.Reader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IReaderService {

    Reader register(Reader reader);

    Reader update(Reader reader);

    Optional<Reader> findById(long id);

    Optional<Reader> findByChatId(long chatId);

    List<Reader> findAll();

    List<Reader> findAllEnabled();

    void enableSleeping();

    Reader updateStatus(long chatId, Reader.Status status);

    Optional<Reader> delete(long id);
}
