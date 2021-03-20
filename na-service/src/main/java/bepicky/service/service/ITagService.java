package bepicky.service.service;

import bepicky.service.entity.Tag;

import java.util.Optional;

public interface ITagService {

    Tag create(String value);

    Tag save(Tag tag);

    Tag get(String value);

    Optional<Tag> findByValue(String value);

}
