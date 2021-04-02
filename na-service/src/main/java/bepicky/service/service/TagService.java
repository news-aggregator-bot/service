package bepicky.service.service;

import bepicky.service.entity.Tag;
import bepicky.service.repository.TagRepository;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagService implements ITagService {
    @Autowired
    private TagRepository repository;

    @Autowired
    private IValueNormalisationService valueNormalisationService;

    @Override
    public Tag create(String value) {
        String normalised = valueNormalisationService.normaliseTag(value);
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

    @Override
    public Set<Tag> findByTitle(String title) {
        return Arrays.stream(title.split("\\s+"))
            .map(valueNormalisationService::normaliseTag)
            .map(repository::findByNormalisedValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

}
