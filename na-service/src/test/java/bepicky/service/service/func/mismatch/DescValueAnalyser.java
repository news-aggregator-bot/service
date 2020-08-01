package bepicky.service.service.func.mismatch;

import bepicky.service.entity.NewsNote;

public class DescValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(NewsNote expected, NewsNote actual) {
        if (expected.getDescription() != null && actual.getDescription() != null) {
            return expected.getDescription().equals(actual.getDescription())
                ? null :
                mismatchMsg(expected.getDescription(), actual.getDescription());
        }
        return null;
    }

    @Override
    public String key() {
        return "description";
    }
}
