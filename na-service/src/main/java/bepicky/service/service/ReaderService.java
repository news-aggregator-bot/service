package bepicky.service.service;

import bepicky.service.entity.Reader;
import bepicky.service.nats.publisher.AdminMessagePublisher;
import bepicky.service.repository.ReaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ReaderService implements IReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private AdminMessagePublisher adminMessagePublisher;

    @Value("${na.reader.tag.limit}")
    private Long tagLimit;

    @Override
    public Reader register(Reader reader) {
        if (reader.getChatId() == null) {
            adminMessagePublisher.publish("FAILED REGISTRATION:reader:no chat id " + reader.toString());
            throw new IllegalArgumentException(reader.toString() + " no chat id");
        }
        Reader repoReader = findByChatId(reader.getChatId()).map(r -> {
            r.setFirstName(reader.getFirstName());
            r.setLastName(reader.getLastName());
            r.setPrimaryLanguage(reader.getPrimaryLanguage());
            r.setUsername(reader.getUsername());
            return r;
        }).orElseGet(() -> {
            reader.setTagsLimit(tagLimit);
            reader.setStatus(Reader.Status.DISABLED);
            return reader;
        });
        log.info("reader:save:{}", reader);
        try {
            Reader saved = readerRepository.save(repoReader);
            adminMessagePublisher.publish("SUCCESS REGISTRATION:reader" + saved.toString());
            return saved;
        } catch (Exception e) {
            adminMessagePublisher.publish("FAILED REGISTRATION:reader:" + reader.toString() + ":" + e
                .getMessage());
            log.error("reader:{}:registration failed", reader, e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Reader update(Reader reader) {
        return readerRepository.save(reader);
    }

    @Override
    public Optional<Reader> findById(long id) {
        return readerRepository.findById(id);
    }

    @Override
    public Optional<Reader> findByChatId(long chatId) {
        return readerRepository.findByChatId(chatId);
    }

    @Override
    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    @Override
    public List<Reader> findAllEnabled() {
        return readerRepository.findAllByStatus(Reader.Status.ENABLED);
    }

    @Override
    public void enableSleeping() {
        Date now = new Date(System.currentTimeMillis());
        Calendar oneHourBefore = Calendar.getInstance();
        oneHourBefore.setTime(now);
        oneHourBefore.add(Calendar.HOUR, -1);
        List<Reader> enabledSleepers = readerRepository.findAllByStatus(Reader.Status.IN_SETTINGS)
            .stream()
            .filter(r -> r.getUpdateDate().before(oneHourBefore.getTime()))
            .map(r -> {
                r.setStatus(Reader.Status.ENABLED);
                return readerRepository.save(r);
            }).collect(Collectors.toList());
        log.info("reader:sleepers enabled " + enabledSleepers.size());
    }

    @Override
    public Reader updateStatus(long chatId, Reader.Status status) {
        return findByChatId(chatId).map(r -> {
            r.setStatus(status);
            log.info("reader:{}:update:status:{}", chatId, status);
            adminMessagePublisher.publish("UPDATE STATUS:reader:" + r.getChatId() + ":" + r.getStatus());
            return readerRepository.save(r);
        }).orElse(null);
    }

    @Override
    public Optional<Reader> delete(long id) {
        return readerRepository.findById(id)
            .map(r -> {
                readerRepository.delete(r);
                return r;
            });
    }
}
