package bepicky.service.repository;

import bepicky.service.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bepicky.service.entity.Reader;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {

    Optional<Reader> findByChatId(long chatId);

    List<Reader> findAllByStatus(Reader.Status status);

    List<Reader> findAllByPrimaryLanguageAndStatus(Language language, Reader.Status status);
}
