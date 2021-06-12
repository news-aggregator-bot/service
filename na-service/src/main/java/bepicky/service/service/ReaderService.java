package bepicky.service.service;

import bepicky.service.entity.Reader;
import bepicky.service.repository.ReaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ReaderService implements IReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    @Value("${na.reader.tag.limit}")
    private Long tagLimit;

    @Override
    public Reader save(Reader reader) {
        Reader repoReader = findByChatId(reader.getChatId()).map(r -> {
            r.setFirstName(reader.getFirstName());
            r.setLastName(reader.getLastName());
            r.setPrimaryLanguage(reader.getPrimaryLanguage());
            r.setUsername(reader.getUsername());
            r.setTagsLimit(tagLimit);
            return r;
        }).orElse(reader);
        log.info("reader:save:{}", reader);
        return readerRepository.save(repoReader);
    }

    @Override
    public Collection<Reader> saveAll(Collection<Reader> readers) {
        return readerRepository.saveAll(readers);
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
    public Reader updateStatus(long chatId, Reader.Status status) {
        return findByChatId(chatId).map(r -> {
            r.setStatus(status);
            log.info("reader:{}:update:status:{}", chatId, status);
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
