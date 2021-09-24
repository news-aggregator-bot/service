package bepicky.service.service.func.mismatch;

import bepicky.service.entity.NewsNoteEntity;

public class AuthorValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(NewsNoteEntity expected, NewsNoteEntity actual) {
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
