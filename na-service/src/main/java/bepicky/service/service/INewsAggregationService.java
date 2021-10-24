package bepicky.service.service;

import bepicky.service.domain.NewsSyncResult;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.SourcePage;

import java.util.Set;

public interface INewsAggregationService {

    NewsSyncResult read(SourcePage sourcePage);

    Set<NewsNote> readFreshNews(SourcePage sourcePage);
}
