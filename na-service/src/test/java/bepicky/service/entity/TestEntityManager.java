package bepicky.service.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

public class TestEntityManager {
    private static final String URL = "url";

    public static SourceEntity source(String name) {
        SourceEntity src = new SourceEntity();
        src.setName(name);
        src.setStatus(SourceEntity.Status.PRIMARY);
        return src;
    }

    public static SourcePageEntity page(List<CategoryEntity> categories, Set<LanguageEntity> languages, SourceEntity source) {
        SourcePageEntity sp = new SourcePageEntity();
        sp.setCategories(categories);
        sp.setLanguages(languages);
        sp.setSource(source);
        return sp;
    }

    public static CategoryEntity region(String name, Set<ReaderEntity> readers) {
        return category(CategoryType.REGION, name, readers);
    }

    public static CategoryEntity common(String name, Set<ReaderEntity> readers) {
        return category(CategoryType.COMMON, name, readers);
    }

    public static CategoryEntity category(CategoryType type, String name, Set<ReaderEntity> readers) {
        CategoryEntity c = new CategoryEntity();
        c.setType(type);
        c.setName(name);
        c.setReaders(readers);
        return c;
    }

    public static ReaderEntity reader(LanguageEntity l, Set<LanguageEntity> languages, Set<SourceEntity> sources) {
        ReaderEntity r = new ReaderEntity();
        r.setId(System.currentTimeMillis());
        r.setPrimaryLanguage(l);
        r.setLanguages(languages);
        r.setSources(sources);
        r.setStatus(ReaderEntity.Status.ENABLED);
        return r;
    }

    public static LanguageEntity en() {
        return language("en");
    }

    public static LanguageEntity ua() {
        return language("ua");
    }

    public static LanguageEntity language(String lang) {
        LanguageEntity l = new LanguageEntity();
        l.setLang(lang);
        return l;
    }

    public static NewsNoteEntity note(String title, SourcePageEntity sp) {
        return note(title, URL, sp, new Date());
    }

    public static NewsNoteEntity note(String title, Date creation) {
        return note(title, URL, null, creation);
    }

    public static NewsNoteEntity note(String title, String url, SourcePageEntity sp, Date creation) {
        NewsNoteEntity n = new NewsNoteEntity();
        n.setId(System.currentTimeMillis());
        n.setTitle(title);
        n.setNormalisedTitle(title);
        n.setUrl(url);
        n.addSourcePage(sp);
        n.setCreationDate(creation);
        n.setUpdateDate(creation);
        return n;
    }

    public static Date before(int months) {
        Calendar fewMonthsAgo = new GregorianCalendar();
        fewMonthsAgo.add(Calendar.MONTH, -months);
        return fewMonthsAgo.getTime();
    }
}
