package bepicky.service.service;

import bepicky.service.entity.Tag;

public interface ITagService {

    Tag save(Tag tag);

    Tag delete(Long id);
}
