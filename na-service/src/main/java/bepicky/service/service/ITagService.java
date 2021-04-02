package bepicky.service.service;

import bepicky.service.entity.Tag;

import java.util.Optional;
import java.util.Set;

public interface ITagService {

    Tag create(String value);

    Tag save(Tag tag);

    Tag get(String value);

    Optional<Tag> findByValue(String value);

    Set<Tag> findByTitle(String title);

}
