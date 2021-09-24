package bepicky.service.service;

import bepicky.service.entity.LanguageEntity;
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
    public Page<LanguageEntity> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<LanguageEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public LanguageEntity save(LanguageEntity language) {
        log.info("language:save:{}", language);
        return repository.save(language);
    }

    @Override
    public List<LanguageEntity> saveAll(Collection<LanguageEntity> languages) {
        log.info("language:save:{}", languages);
        return repository.saveAll(languages);
    }

    @Override
    public Optional<LanguageEntity> find(String name) {
        return repository.findById(name);
    }

    @Override
    public LanguageEntity getDefault() {
        return repository.findById("ukr").get();
    }

    @Override
    public long countAll() {
        return repository.count();
    }
}
