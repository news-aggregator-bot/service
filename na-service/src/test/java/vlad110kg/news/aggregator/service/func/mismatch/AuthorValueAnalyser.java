package vlad110kg.news.aggregator.service.func.mismatch;

import vlad110kg.news.aggregator.entity.NewsNote;

public class AuthorValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(NewsNote expected, NewsNote actual) {
        if (expected.getAuthor() != null && actual.getAuthor() != null) {
            return expected.getAuthor().equals(actual.getAuthor()) ?
                null :
                mismatchMsg(expected.getAuthor(), actual.getAuthor());
        }
        return null;
    }

    @Override
    public String key() {
        return "author";
    }
}
