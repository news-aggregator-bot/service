package bepicky.service.service.func.mismatch;

import bepicky.service.entity.NewsNoteEntity;

public class UrlValueAnalyser implements ValueAnalyzer {

    @Override
    public String analyse(NewsNoteEntity expected, NewsNoteEntity actual) {
        return expected.getUrl().equals(actual.getUrl()) ? null : mismatchMsg(expected.getUrl(), actual.getUrl());
    }

    @Override
    public String key() {
        return "url";
    }
}
