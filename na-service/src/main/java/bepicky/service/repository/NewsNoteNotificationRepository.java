package bepicky.service.repository;

import bepicky.service.entity.NewsNoteNotificationEntity;
import bepicky.service.entity.NewsNoteNotificationIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsNoteNotificationRepository extends JpaRepository<NewsNoteNotificationEntity, NewsNoteNotificationIdEntity> {

    List<NewsNoteNotificationEntity> findAllByIdReaderIdAndState(long readerId, NewsNoteNotificationEntity.State state);

    Optional<NewsNoteNotificationEntity> findByIdReaderIdAndIdNoteId(long readerId, long noteId);

    Set<NewsNoteNotificationEntity> findByCreationDateBefore(Date limit);
}
