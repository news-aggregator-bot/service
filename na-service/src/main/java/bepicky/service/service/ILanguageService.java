package bepicky.service.service;

import org.springframework.data.domain.Pageable;
import bepicky.service.entity.Language;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ILanguageService {

    List<Language> listAll(Pageable pageable);

    Language save(Language language);

    List<Language> saveAll(Collection<Language> languages);

    Optional<Language> find(String name);

    long countAll();
}
