package bepicky.service.service.func.mismatch;

import bepicky.service.entity.NewsNoteEntity;

public class TitleValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(
        NewsNoteEntity expected, NewsNoteEntity actual
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
