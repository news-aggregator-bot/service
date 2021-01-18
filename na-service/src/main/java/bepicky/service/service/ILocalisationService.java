package bepicky.service.service;

import bepicky.service.entity.Localisation;

import java.util.Collection;
import java.util.List;

public interface ILocalisationService {
    List<Localisation> saveAll(Collection<Localisation> categories);

    List<Localisation> findByValue(String value);

    List<Localisation> getAll();

    void deleteAll(Collection<Localisation> localisations);

    void delete(Localisation categoryLocalisation);
}
