package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.entity.Tag;

public interface ITagService {

    Tag save(Tag tag);

    Tag delete(Long id);
}
