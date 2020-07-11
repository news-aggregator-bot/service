package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.entity.NewsNote;

import java.util.Collection;
import java.util.Optional;

public interface INewsNoteService {

    NewsNote save(NewsNote note);

    Collection<NewsNote> saveAll(Collection<NewsNote> notes);

    Optional<NewsNote> find(Long id);

    Optional<NewsNote> findByUrl(String url);

    boolean exists(String url);
}
