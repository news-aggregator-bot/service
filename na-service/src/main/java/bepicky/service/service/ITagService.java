package bepicky.service.service;

import bepicky.service.entity.TagEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITagService {

    TagEntity create(String value);

    TagEntity save(TagEntity tag);

    TagEntity get(String value);

    List<TagEntity> findAllByValue(String value);

    Optional<TagEntity> findByValue(String value);

    Set<TagEntity> findByTitle(String title);

    Optional<TagEntity> findById(Long id);

}
