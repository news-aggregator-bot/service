package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.entity.Reader;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IReaderService {

    Reader save(Reader reader);

    Collection<Reader> save(Collection<Reader> reader);

    Optional<Reader> find(long chatId);

    List<Reader> findAll();

    List<Reader> findAllEnabled();

    boolean enable(long chatId);

    boolean disable(long chatId);
}
