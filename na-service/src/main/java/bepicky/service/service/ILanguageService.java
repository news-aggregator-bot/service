package bepicky.service.service;

import bepicky.service.entity.LanguageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ILanguageService {

    Page<LanguageEntity> listAll(Pageable pageable);

    List<LanguageEntity> getAll();

    LanguageEntity save(LanguageEntity language);

    List<LanguageEntity> saveAll(Collection<LanguageEntity> languages);

    Optional<LanguageEntity> find(String name);

    LanguageEntity getDefault();

    long countAll();
}
