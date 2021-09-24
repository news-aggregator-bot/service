package bepicky.service.service;

import bepicky.service.entity.ReaderEntity;

import java.util.List;
import java.util.Optional;

public interface IReaderService {

    ReaderEntity register(ReaderEntity reader);

    ReaderEntity update(ReaderEntity reader);

    Optional<ReaderEntity> findById(long id);

    Optional<ReaderEntity> findByChatId(long chatId);

    List<ReaderEntity> findAll();

    List<ReaderEntity> findAllEnabled();

    void enableSleeping();

    ReaderEntity updateStatus(long chatId, ReaderEntity.Status status);

    Optional<ReaderEntity> delete(long id);
}
