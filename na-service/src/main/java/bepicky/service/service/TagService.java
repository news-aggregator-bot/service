package bepicky.service.service;

import bepicky.service.entity.TagEntity;
import bepicky.service.repository.TagRepository;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
    public TagEntity create(String value) {
        String normalised = valueNormalisationService.normaliseTag(value);
        TagEntity tag = new TagEntity();
        tag.setValue(value);
        tag.setNormalisedValue(normalised);
        log.info("tag:new:{}", tag);
        return repository.save(tag);
    }

    @Override
    public TagEntity save(TagEntity tag) {
        log.info("tag:save:{}", tag);
        return repository.save(tag);
    }

    @Override
    public TagEntity get(String value) {
        return repository.findByNormalisedValue(valueNormalisationService.normaliseTag(value))
            .orElseGet(() -> create(value));
    }

    @Override
    public List<TagEntity> findAllByValue(String value) {
        String normalisedVal = valueNormalisationService.normaliseTag(value);
        return repository.findByNormalisedValueContains(normalisedVal);
    }

    @Override
    public Optional<TagEntity> findByValue(String value) {
        return repository.findByNormalisedValue(value);
    }

    @Override
    public Set<TagEntity> findByTitle(String title) {
        return Arrays.stream(title.split("\\s+"))
            .map(valueNormalisationService::normaliseTag)
            .map(repository::findByNormalisedValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<TagEntity> findById(Long id) {
        return repository.findById(id);
    }

}
