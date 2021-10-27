package bepicky.service.service;

import bepicky.service.entity.NewsNote;

import java.util.List;
import java.util.Set;

public interface INewsAggregationService {

    Set<NewsNote> aggregate(String url, List<String> content);
}
