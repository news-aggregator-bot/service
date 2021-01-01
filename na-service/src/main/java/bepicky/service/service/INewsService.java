package bepicky.service.service;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.SourcePage;

import java.util.Set;

public interface INewsService {

    NewsSyncResult sync(String name);

    NewsSyncResult read(SourcePage sourcePage);

    Set<NewsNote> readFreshNews(SourcePage sourcePage);
}
