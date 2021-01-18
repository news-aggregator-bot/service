package bepicky.service.service;

import bepicky.service.entity.Localisation;
import bepicky.service.repository.LocalisationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class LocalisationService implements ILocalisationService{

    @Autowired
    private LocalisationRepository localisationRepository;

    @Override
    public List<Localisation> saveAll(Collection<Localisation> categories) {
        log.info("localisation:save:{}", categories);
        return localisationRepository.saveAll(categories);
    }

    @Override
    public List<Localisation> findByValue(String value) {
        return localisationRepository.findByValue(value);
    }

    @Override
    public List<Localisation> getAll() {
        return localisationRepository.findAll();
    }

    @Override
    public void deleteAll(Collection<Localisation> localisations) {
        localisationRepository.deleteAll(localisations);
    }

    @Override
    public void delete(Localisation categoryLocalisation) {
        log.info("localisation:delete:{}", categoryLocalisation);
        localisationRepository.delete(categoryLocalisation);
    }
}
