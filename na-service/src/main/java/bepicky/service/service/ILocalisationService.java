package bepicky.service.service;

import bepicky.service.entity.LocalisationEntity;

import java.util.Collection;
import java.util.List;

public interface ILocalisationService {
    List<LocalisationEntity> saveAll(Collection<LocalisationEntity> categories);

    List<LocalisationEntity> findByValue(String value);

    List<LocalisationEntity> getAll();

    void deleteAll(Collection<LocalisationEntity> localisations);

    void delete(LocalisationEntity categoryLocalisation);
}
