package bepicky.service.repository;

import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.NewsNoteNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsNoteNotificationRepository extends JpaRepository<NewsNoteNotification, NewsNoteNotificationId> {

    List<NewsNoteNotification> findAllByIdReaderIdAndState(long readerId, NewsNoteNotification.State state);
}
