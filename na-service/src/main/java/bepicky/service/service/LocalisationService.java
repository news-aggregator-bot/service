package bepicky.service.service;

import bepicky.service.entity.LocalisationEntity;
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
    public List<LocalisationEntity> saveAll(Collection<LocalisationEntity> categories) {
        return localisationRepository.saveAll(categories);
    }

    @Override
    public List<LocalisationEntity> findByValue(String value) {
        return localisationRepository.findByValue(value);
    }

    @Override
    public List<LocalisationEntity> getAll() {
        return localisationRepository.findAll();
    }

    @Override
    public void deleteAll(Collection<LocalisationEntity> localisations) {
        localisationRepository.deleteAll(localisations);
    }

    @Override
    public void delete(LocalisationEntity categoryLocalisation) {
        log.info("localisation:delete:{}", categoryLocalisation);
        localisationRepository.delete(categoryLocalisation);
    }
}
