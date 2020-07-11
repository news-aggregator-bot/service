package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.domain.NewsSyncResult;
import vlad110kg.news.aggregator.entity.SourcePage;

public interface INewsService {

    NewsSyncResult sync(String name);

    NewsSyncResult sync(SourcePage sourcePage);
}
