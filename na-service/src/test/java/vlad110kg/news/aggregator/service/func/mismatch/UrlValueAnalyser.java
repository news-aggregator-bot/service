package vlad110kg.news.aggregator.service.func.mismatch;

import vlad110kg.news.aggregator.entity.NewsNote;

public class UrlValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(NewsNote expected, NewsNote actual) {
        return expected.getUrl().equals(actual.getUrl()) ? null : mismatchMsg(expected.getUrl(), actual.getUrl());
    }

    @Override
    public String key() {
        return "url";
    }
}
