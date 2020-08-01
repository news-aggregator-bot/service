package bepicky.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bepicky.service.entity.NewsNote;
import bepicky.service.repository.NewsNoteRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class NewsNoteService implements INewsNoteService {

    @Autowired
    private NewsNoteRepository repository;

    @Override
    @Transactional
    public NewsNote save(NewsNote note) {
        log.info("news:save:{}", note);
        return repository.saveAndFlush(note);
    }

    @Override
    public Collection<NewsNote> saveAll(Collection<NewsNote> note) {
        log.info("news:save:{}", note);
        return repository.saveAll(note);
    }

    @Override
    public Optional<NewsNote> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<NewsNote> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    @Override
    public boolean exists(String url) {
        return repository.existsByUrl(url);
    }
}
