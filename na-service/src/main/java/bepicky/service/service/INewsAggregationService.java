package bepicky.service.service;

import bepicky.service.domain.RawNews;
import bepicky.service.entity.NewsNote;

import java.util.Set;

public interface INewsAggregationService {

    Set<NewsNote> aggregate(RawNews news);
}
