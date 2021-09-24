package bepicky.service.service.func.mismatch;

import bepicky.service.entity.NewsNoteEntity;

public interface ValueAnalyzer {

    String MISMATCH_PATTERN = "%s:expected: %s is different from actual: %s";

    String analyse(NewsNoteEntity expected, NewsNoteEntity actual);

    String key();

    default String mismatchMsg(String expected, String actual) {
        return String.format(MISMATCH_PATTERN, key(), expected, actual);
    }
}
