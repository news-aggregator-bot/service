package bepicky.service.schedule;

import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.entity.Category;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsNoteService;
import bepicky.service.service.IReaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static bepicky.service.entity.TestEntityManager.common;
import static bepicky.service.entity.TestEntityManager.en;
import static bepicky.service.entity.TestEntityManager.note;
import static bepicky.service.entity.TestEntityManager.page;
import static bepicky.service.entity.TestEntityManager.reader;
import static bepicky.service.entity.TestEntityManager.region;
import static bepicky.service.entity.TestEntityManager.source;
import static bepicky.service.entity.TestEntityManager.ua;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("it")
public class NewsSynchroniserTest {

    @Autowired
    private NewsSynchroniser newsSynchroniser;

    @MockBean
    private INewsNoteService newsNoteService;

    @MockBean
    private IReaderService readerService;

    @Test
    public void sync_NotesApplicable_ShouldFillUserQueueWithNotes() {
        Source src = source("name");
        Language en = en();
        Reader r1 = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of(r1));
        Category c1 = common("c1", Set.of(r1));
        List<Category> categories = List.of(uk, c1);
        r1.setCategories(Set.of(uk, c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        assertNull(r1.getNotifyQueue());
        newsSynchroniser.sync();

        Set<NewsNote> queue = r1.getNotifyQueue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
    }

    private void mockServiceNotes(Set<NewsNote> notes) {
        when(newsNoteService.getTodayNotes()).thenReturn(notes);
        when(newsNoteService.getAllAfter(any())).thenReturn(notes);
    }

    @Test
    public void sync_NotesNotApplicableByReaderLanguage_ShouldNotFillNotApplicableUsersNewsNoteQueue() {
        Source src = source("name");
        Language en = en();
        Language ua = ua();
        Reader applicable = reader(en, Set.of(en), Set.of(src));
        Reader notApplicable = reader(ua, Set.of(ua), Set.of(src));
        Set<Reader> readers = Set.of(applicable, notApplicable);
        Category uk = region("UK", readers);
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        applicable.setCategories(Set.of(uk, c1));
        notApplicable.setCategories(Set.of(uk, c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        assertNull(applicable.getNotifyQueue());
        assertNull(notApplicable.getNotifyQueue());
        newsSynchroniser.sync();

        Set<NewsNote> queue = applicable.getNotifyQueue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
        assertNull(notApplicable.getNotifyQueue());
    }

    @Test
    public void sync_NotesNotApplicableByReaderRegion_ShouldNotFillNotApplicableUsersNewsNoteQueue() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<Reader> readers = Set.of(notApplicable);
        Category uk = region("UK", Set.of());
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(c1));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        assertNull(notApplicable.getNotifyQueue());
        newsSynchroniser.sync();

        assertNull(notApplicable.getNotifyQueue());
    }

    @Test
    public void sync_NotesNotApplicableByReaderCategory_ShouldNotFillNotApplicableUsersNewsNoteQueue() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<Reader> readers = Set.of(notApplicable);
        Category uk = region("UK", readers);
        Category c1 = common("c1", readers);
        List<Category> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(uk));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        assertNull(notApplicable.getNotifyQueue());
        newsSynchroniser.sync();

        assertNull(notApplicable.getNotifyQueue());
    }

    @Test
    public void sync_EmptyCategoryReaders_ShouldNotFillNotApplicableUsersNewsNoteQueue() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of());
        List<Category> categories = List.of(uk);
        notApplicable.setCategories(Set.of(uk));

        SourcePage page = page(categories, Set.of(en), src);

        NewsNote note1 = note("t1", page);
        NewsNote note2 = note("t2", page);

        Set<NewsNote> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        assertNull(notApplicable.getNotifyQueue());
        newsSynchroniser.sync();

        assertNull(notApplicable.getNotifyQueue());
    }

    @Test
    public void sync_EmptyActualNotes_ShouldNotFillNotApplicableUsersNewsNoteQueue() {
        Source src = source("name");
        Language en = en();
        Reader notApplicable = reader(en, Set.of(en), Set.of(src));
        Category uk = region("UK", Set.of());
        notApplicable.setCategories(Set.of(uk));

        mockServiceNotes(Set.of());

        assertNull(notApplicable.getNotifyQueue());
        newsSynchroniser.sync();

        assertNull(notApplicable.getNotifyQueue());
    }

    @TestConfiguration
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    static class NewsSynchroniserTestConfiguration {

        @Bean
        public NewsSynchroniser newsSynchroniser() {
            return new NewsSynchroniser();
        }
    }
}