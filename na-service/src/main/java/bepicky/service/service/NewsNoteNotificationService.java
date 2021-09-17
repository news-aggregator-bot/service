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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NewsNoteNotificationService implements INewsNoteNotificationService {

    @Autowired
    private NewsNoteNotificationRepository repository;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private INewsNoteService noteService;

    @Override
    public NewsNoteNotification saveSingleNew(Reader reader, NewsNote note, NewsNoteNotification.Link link, String key) {
        NewsNoteNotification notification = new NewsNoteNotification(reader, note);
        if (repository.existsById(notification.getId())) {
            return null;
        }
        notification.setLink(link);
        notification.setLinkKey(key);
        return repository.save(notification);
    }

    @Override
    public List<NewsNoteNotification> saveNew(
        Reader reader, Collection<NewsNote> notes
    ) {
        List<NewsNoteNotification> newNoteNotifications = notes.stream()
            .map(n -> {
                NewsNoteNotification notification = new NewsNoteNotification(reader, n);
                if (repository.existsById(notification.getId())) {
                    return null;
                }
                notification.setLink(NewsNoteNotification.Link.CATEGORY);
                return notification;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return repository.saveAll(newNoteNotifications);
    }

    @Override
    public List<NewsNoteNotification> findAllNew(Reader reader) {
        return repository.findAllByIdReaderIdAndState(reader.getId(), NewsNoteNotification.State.NEW);
    }

    @Override
    public Optional<NewsNoteNotification> find(
        Reader reader, NewsNote note
    ) {
        NewsNoteNotificationId id = id(reader, note);
        return repository.findById(id);
    }

    private NewsNoteNotificationId id(Reader reader, NewsNote note) {
        return new NewsNoteNotificationId(reader.getId(), note.getId());
    }

    @Override
    public boolean exists(Reader reader, NewsNote note) {
        return repository.existsById(id(reader, note));
    }

    @Override
    public NewsNoteNotification sent(Long chatId, Long noteId) {
        Reader r = readerService.findByChatId(chatId)
            .orElseThrow(() -> new IllegalArgumentException("reader:404:" + chatId));
        NewsNote n = noteService.find(noteId)
            .orElseThrow(() -> new IllegalArgumentException("newnote:404:" + noteId));

        NewsNoteNotification notification = find(r, n)
            .orElseThrow(() -> new IllegalArgumentException("newnote:notification:404: reader id " + r.getId() + ": note id " + n
            .getId()));
        notification.setState(NewsNoteNotification.State.SENT);
        return repository.save(notification);
    }

    @Override
    public void archiveOld() {
        Calendar twoDaysAgo = new GregorianCalendar();
        twoDaysAgo.add(Calendar.DAY_OF_MONTH, -2);
        Set<NewsNoteNotification> oldNotifications = repository.findByCreationDateBefore(twoDaysAgo.getTime());
        log.info("news_note_notification:archive:{}", oldNotifications.size());
        repository.deleteAll(oldNotifications);
    }
}
