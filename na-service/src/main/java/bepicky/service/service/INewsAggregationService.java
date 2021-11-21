package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;

import java.util.Set;

public interface INewsAggregationService {

    Set<NewsNote> aggregate(RawNews news);

    Set<NewsNote> aggregateExisting(long latestNoteId);

    Set<NewsNote> aggregateLatest(Reader reader);

}
