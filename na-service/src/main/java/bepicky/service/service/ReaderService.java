package bepicky.service.service;

import bepicky.service.entity.Reader;
import bepicky.service.repository.ReaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReaderService implements IReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    @Override
    public Reader save(Reader reader) {
        Reader repoReader = find(reader.getChatId()).orElse(reader);
        log.info("reader:save:{}", reader);
        return readerRepository.save(repoReader);
    }

    @Override
    public Collection<Reader> save(Collection<Reader> readers) {
        return readerRepository.saveAll(readers);
    }

    @Override
    public Optional<Reader> find(long chatId) {
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
    public boolean enable(long chatId) {
        return find(chatId).map(r -> {
            r.setStatus(Reader.Status.ENABLED);
            log.info("reader:enable:{}", r);
            return readerRepository.save(r);
        }).isPresent();
    }

    @Override
    public boolean disable(long chatId) {
        return find(chatId).map(r -> {
            r.setStatus(Reader.Status.DISABLED);
            log.info("reader:disable:{}", r);
            return readerRepository.save(r);
        }).isPresent();
    }
}