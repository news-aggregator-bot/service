package bepicky.service.service;

import bepicky.service.entity.Language;
import bepicky.service.repository.LanguageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class LanguageService implements ILanguageService {

    @Autowired
    private LanguageRepository repository;

    @Override
    public Page<Language> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Language> getAll() {
        return repository.findAll();
    }

    @Override
    public Language save(Language language) {
        log.info("language:save:{}", language);
        return repository.save(language);
    }

    @Override
    public List<Language> saveAll(Collection<Language> languages) {
        log.info("language:save:{}", languages);
        return repository.saveAll(languages);
    }

    @Override
    public Optional<Language> find(String name) {
        return repository.findById(name);
    }

    @Override
    public Language getDefault() {
        return repository.findById("en").get();
    }

    @Override
    public long countAll() {
        return repository.count();
    }
}
