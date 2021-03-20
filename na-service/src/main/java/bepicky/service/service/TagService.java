package bepicky.service.service;

import bepicky.service.entity.Tag;
import bepicky.service.repository.TagRepository;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TagService implements ITagService {
    @Autowired
    private TagRepository repository;

    @Autowired
    private IValueNormalisationService valueNormalisationService;

    @Override
    public Tag save(String value) {
        String normalised = valueNormalisationService.normaliseValue(value);
        Tag tag = new Tag();
        tag.setValue(value);
        tag.setNormalisedValue(normalised);
        log.info("tag:new:{}", tag);
        return repository.save(tag);
    }

    @Override
    public Optional<Tag> findByValue(String value) {
        return Optional.empty();
    }

}
