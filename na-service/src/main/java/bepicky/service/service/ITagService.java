package bepicky.service.service;

import bepicky.service.entity.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITagService {

    Tag create(String value);

    Tag save(Tag tag);

    Tag get(String value);

    List<Tag> findByValue(String value);

    Set<Tag> findByTitle(String title);

    Optional<Tag> findById(Long id);

}
