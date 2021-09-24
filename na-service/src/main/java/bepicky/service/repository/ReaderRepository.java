package bepicky.service.repository;

import bepicky.service.entity.ReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<ReaderEntity, Long> {

    Optional<ReaderEntity> findByChatId(long chatId);

    List<ReaderEntity> findAllByStatus(ReaderEntity.Status status);

}
