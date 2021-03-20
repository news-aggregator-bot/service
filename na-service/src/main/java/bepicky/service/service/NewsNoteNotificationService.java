package bepicky.service.service;

import bepicky.service.entity.NewsNote;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.NewsNoteNotificationId;
import bepicky.service.entity.Reader;
import bepicky.service.repository.NewsNoteNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NewsNoteNotificationService implements INewsNoteNotificationService {

    @Autowired
    private NewsNoteNotificationRepository notificationRepository;

    @Override
    public NewsNoteNotification saveNew(Reader reader, NewsNote note) {
        NewsNoteNotification notification = new NewsNoteNotification(reader, note);
        if (notificationRepository.existsById(notification.getId())) {
            return null;
        }
        log.info("news_note_notification:save_new:{}", notification);
        return notificationRepository.save(notification);
    }

    @Override
    public List<NewsNoteNotification> saveNew(
        Reader reader, Collection<NewsNote> notes
    ) {
        List<NewsNoteNotification> newNoteNotifications = notes.stream()
            .map(n -> {
                NewsNoteNotification notification = new NewsNoteNotification(reader, n);
                if (notificationRepository.existsById(notification.getId())) {
                    return null;
                }
                return notification;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return notificationRepository.saveAll(newNoteNotifications);
    }

    @Override
    public List<NewsNoteNotification> findNew(Reader reader) {
        return notificationRepository.findAllByIdReaderIdAndState(reader.getId(), NewsNoteNotification.State.NEW);
    }

    @Override
    public Optional<NewsNoteNotification> find(
        Reader reader, NewsNote note
    ) {
        NewsNoteNotificationId id = id(reader, note);
        return notificationRepository.findById(id);
    }

    private NewsNoteNotificationId id(Reader reader, NewsNote note) {
        return new NewsNoteNotificationId(reader.getId(), note.getId());
    }

    @Override
    public boolean exists(Reader reader, NewsNote note) {
        return notificationRepository.existsById(id(reader, note));
    }

    @Override
    public NewsNoteNotification sent(NewsNoteNotification notification) {
        notification.setState(NewsNoteNotification.State.SENT);
        return notificationRepository.save(notification);
    }
}
