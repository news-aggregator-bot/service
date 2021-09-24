package bepicky.service.service;

import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.NewsNoteNotificationEntity;
import bepicky.service.entity.NewsNoteNotificationIdEntity;
import bepicky.service.entity.ReaderEntity;
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
    public NewsNoteNotificationEntity saveSingleNew(ReaderEntity reader, NewsNoteEntity note, NewsNoteNotificationEntity.Link link, String key) {
        NewsNoteNotificationEntity notification = new NewsNoteNotificationEntity(reader, note);
        if (repository.existsById(notification.getId())) {
            return null;
        }
        notification.setLink(link);
        notification.setLinkKey(key);
        return repository.save(notification);
    }

    @Override
    public List<NewsNoteNotificationEntity> saveNew(
        ReaderEntity reader, Collection<NewsNoteEntity> notes
    ) {
        List<NewsNoteNotificationEntity> newNoteNotifications = notes.stream()
            .map(n -> {
                NewsNoteNotificationEntity notification = new NewsNoteNotificationEntity(reader, n);
                if (repository.existsById(notification.getId())) {
                    return null;
                }
                notification.setLink(NewsNoteNotificationEntity.Link.CATEGORY);
                return notification;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return repository.saveAll(newNoteNotifications);
    }

    @Override
    public List<NewsNoteNotificationEntity> findAllNew(ReaderEntity reader) {
        return repository.findAllByIdReaderIdAndState(reader.getId(), NewsNoteNotificationEntity.State.NEW);
    }

    @Override
    public Optional<NewsNoteNotificationEntity> find(
        ReaderEntity reader, NewsNoteEntity note
    ) {
        NewsNoteNotificationIdEntity id = id(reader, note);
        return repository.findById(id);
    }

    private NewsNoteNotificationIdEntity id(ReaderEntity reader, NewsNoteEntity note) {
        return new NewsNoteNotificationIdEntity(reader.getId(), note.getId());
    }

    @Override
    public boolean exists(ReaderEntity reader, NewsNoteEntity note) {
        return repository.existsById(id(reader, note));
    }

    @Override
    public NewsNoteNotificationEntity sent(Long chatId, Long noteId) {
        ReaderEntity r = readerService.findByChatId(chatId)
            .orElseThrow(() -> new IllegalArgumentException("reader:404:" + chatId));
        NewsNoteEntity n = noteService.find(noteId)
            .orElseThrow(() -> new IllegalArgumentException("newnote:404:" + noteId));

        NewsNoteNotificationEntity notification = find(r, n)
            .orElseThrow(() -> new IllegalArgumentException("newnote:notification:404: reader id " + r.getId() + ": note id " + n
            .getId()));
        notification.setState(NewsNoteNotificationEntity.State.SENT);
        return repository.save(notification);
    }

    @Override
    public void archiveOld() {
        Calendar twoDaysAgo = new GregorianCalendar();
        twoDaysAgo.add(Calendar.DAY_OF_MONTH, -2);
        Set<NewsNoteNotificationEntity> oldNotifications = repository.findByCreationDateBefore(twoDaysAgo.getTime());
        log.info("news_note_notification:archive:{}", oldNotifications.size());
        repository.deleteAll(oldNotifications);
    }
}
