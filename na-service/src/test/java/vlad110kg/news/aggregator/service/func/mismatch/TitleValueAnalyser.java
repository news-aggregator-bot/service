package vlad110kg.news.aggregator.service.func.mismatch;

import vlad110kg.news.aggregator.entity.NewsNote;

public class TitleValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(
        NewsNote expected, NewsNote actual
    ) {
        return expected.getTitle().equals(actual.getTitle()) ?
            null :
            mismatchMsg(expected.getTitle(), actual.getTitle());
    }

    @Override
    public String key() {
        return "title";
    }
}
