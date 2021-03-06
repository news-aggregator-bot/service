package bepicky.service.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

public class TestEntityManager {
    private static final String URL = "url";

    public static Source source(String name) {
        Source src = new Source();
        src.setName(name);
        src.setStatus(Source.Status.PRIMARY);
        return src;
    }

    public static SourcePage page(List<Category> categories, Set<Language> languages, Source source) {
        SourcePage sp = new SourcePage();
        sp.setCategories(categories);
        sp.setLanguages(languages);
        sp.setSource(source);
        return sp;
    }

    public static Category region(String name, Set<Reader> readers) {
        return category(CategoryType.REGION, name, readers);
    }

    public static Category common(String name, Set<Reader> readers) {
        return category(CategoryType.COMMON, name, readers);
    }

    public static Category category(CategoryType type, String name, Set<Reader> readers) {
        Category c = new Category();
        c.setType(type);
        c.setName(name);
        c.setReaders(readers);
        return c;
    }

    public static Reader reader(Language l, Set<Language> languages, Set<Source> sources) {
        Reader r = new Reader();
        r.setId(System.currentTimeMillis());
        r.setPrimaryLanguage(l);
        r.setLanguages(languages);
        r.setSources(sources);
        r.setStatus(Reader.Status.ENABLED);
        return r;
    }

    public static Language en() {
        return language("en");
    }

    public static Language ua() {
        return language("ua");
    }

    public static Language language(String lang) {
        Language l = new Language();
        l.setLang(lang);
        return l;
    }

    public static NewsNote note(String title, SourcePage sp) {
        return note(title, URL, sp, new Date());
    }

    public static NewsNote note(String title, Date creation) {
        return note(title, URL, null, creation);
    }

    public static NewsNote note(String title, String url, SourcePage sp, Date creation) {
        NewsNote n = new NewsNote();
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
