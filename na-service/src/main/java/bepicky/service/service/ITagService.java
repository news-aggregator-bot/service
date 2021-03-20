package bepicky.service.service;

import bepicky.service.entity.Tag;

import java.util.Optional;

public interface ITagService {

    Tag save(String value);

    Optional<Tag> findByValue(String value);

}
