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
    public Tag create(String value) {
        String normalised = valueNormalisationService.lettersAndDigits(value);
        Tag tag = new Tag();
        tag.setValue(value);
        tag.setNormalisedValue(normalised);
        log.info("tag:new:{}", tag);
        return repository.save(tag);
    }

    @Override
    public Tag save(Tag tag) {
        log.info("tag:save:{}", tag);
        return repository.save(tag);
    }

    @Override
    public Tag get(String value) {
        return repository.findByNormalisedValue(valueNormalisationService.normaliseTitle(value))
            .orElse(create(value));
    }

    @Override
    public Optional<Tag> findByValue(String value) {
        return Optional.empty();
    }

}
