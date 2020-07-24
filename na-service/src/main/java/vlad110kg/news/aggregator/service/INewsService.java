package vlad110kg.news.aggregator.service;

import vlad110kg.news.aggregator.domain.NewsSyncResult;
import vlad110kg.news.aggregator.entity.NewsNote;
import vlad110kg.news.aggregator.entity.SourcePage;

import java.util.Set;

public interface INewsService {

    NewsSyncResult sync(String name);

    NewsSyncResult sync(SourcePage sourcePage);

    Set<NewsNote> readFreshNews(SourcePage sourcePage);
}
