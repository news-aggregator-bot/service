package bepicky.service.repository;

import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.NewsNoteNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsNoteNotificationRepository extends JpaRepository<NewsNoteNotification, NewsNoteNotificationId> {

    List<NewsNoteNotification> findAllByIdReaderIdAndState(long readerId, NewsNoteNotification.State state);

    Optional<NewsNoteNotification> findByIdReaderIdAndIdNoteId(long readerId, long noteId);

    Set<NewsNoteNotification> findByCreationDateBefore(Date limit);
}
