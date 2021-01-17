package bepicky.service.service;

import bepicky.service.entity.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ILanguageService {

    Page<Language> listAll(Pageable pageable);

    List<Language> getAll();

    Language save(Language language);

    List<Language> saveAll(Collection<Language> languages);

    Optional<Language> find(String name);

    Language getDefault();

    long countAll();
}
