package bepicky.service.schedule;

import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.ReaderEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.INewsNoteService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("it")
public class NewsSynchroniserTest {

    @Autowired
    private NewsSynchroniser newsSynchroniser;

    @MockBean
    private INewsNoteService newsNoteService;

    @MockBean
    private INewsNoteNotificationService notificationService;

    @Captor
    private ArgumentCaptor<Set<NewsNoteEntity>> notificationsAC;

    @Test
    public void sync_ApplicableNotes_ShouldSaveAllNotes() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        ReaderEntity r1 = reader(en, Set.of(en), Set.of(src));
        CategoryEntity uk = region("UK", Set.of(r1));
        CategoryEntity c1 = common("c1", Set.of(r1));
        List<CategoryEntity> categories = List.of(uk, c1);
        r1.setCategories(Set.of(uk, c1));

        SourcePageEntity page = page(categories, Set.of(en), src);

        NewsNoteEntity note1 = note("t1", page);
        NewsNoteEntity note2 = note("t2", page);

        Set<NewsNoteEntity> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        newsSynchroniser.sync();

        verify(notificationService, times(1)).saveNew(eq(r1), notificationsAC.capture());

        Set<NewsNoteEntity> queue = notificationsAC.getValue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
    }

    @Test
    public void sync_NotApplicableNotesByReaderLanguage_ShouldSaveOnlyForApplicableReaders() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        LanguageEntity ua = ua();
        ReaderEntity applicable = reader(en, Set.of(en), Set.of(src));
        ReaderEntity notApplicable = reader(ua, Set.of(ua), Set.of(src));
        Set<ReaderEntity> readers = Set.of(applicable, notApplicable);
        CategoryEntity uk = region("UK", readers);
        CategoryEntity c1 = common("c1", readers);
        List<CategoryEntity> categories = List.of(uk, c1);
        applicable.setCategories(Set.of(uk, c1));
        notApplicable.setCategories(Set.of(uk, c1));

        SourcePageEntity page = page(categories, Set.of(en), src);

        NewsNoteEntity note1 = note("t1", page);
        NewsNoteEntity note2 = note("t2", page);

        Set<NewsNoteEntity> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        newsSynchroniser.sync();

        verify(notificationService, times(1)).saveNew(eq(applicable), notificationsAC.capture());
        verify(notificationService, never()).saveNew(eq(notApplicable), anySet());

        Set<NewsNoteEntity> queue = notificationsAC.getValue();
        assertEquals(2, queue.size());
        assertEquals(notes, queue);
    }

    @Test
    public void sync_NotApplicableNotesByReaderRegion_ShouldNotSaveAnyNotes() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        ReaderEntity notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<ReaderEntity> readers = Set.of(notApplicable);
        CategoryEntity uk = region("UK", Set.of());
        CategoryEntity c1 = common("c1", readers);
        List<CategoryEntity> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(c1));

        SourcePageEntity page = page(categories, Set.of(en), src);

        NewsNoteEntity note1 = note("t1", page);
        NewsNoteEntity note2 = note("t2", page);

        Set<NewsNoteEntity> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        newsSynchroniser.sync();

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_NotApplicableNotesByReaderCategory_ShouldNotSaveAnyNotes() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        ReaderEntity notApplicable = reader(en, Set.of(en), Set.of(src));
        Set<ReaderEntity> readers = Set.of(notApplicable);
        CategoryEntity uk = region("UK", readers);
        CategoryEntity c1 = common("c1", readers);
        List<CategoryEntity> categories = List.of(uk, c1);
        notApplicable.setCategories(Set.of(uk));

        SourcePageEntity page = page(categories, Set.of(en), src);

        NewsNoteEntity note1 = note("t1", page);
        NewsNoteEntity note2 = note("t2", page);

        Set<NewsNoteEntity> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        newsSynchroniser.sync();

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_EmptyCategoryReaders_ShouldNotSaveNotApplicableNewsNotes() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        ReaderEntity notApplicable = reader(en, Set.of(en), Set.of(src));
        CategoryEntity uk = region("UK", Set.of());
        List<CategoryEntity> categories = List.of(uk);
        notApplicable.setCategories(Set.of(uk));

        SourcePageEntity page = page(categories, Set.of(en), src);

        NewsNoteEntity note1 = note("t1", page);
        NewsNoteEntity note2 = note("t2", page);

        Set<NewsNoteEntity> notes = Set.of(note1, note2);

        mockServiceNotes(notes);

        newsSynchroniser.sync();

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    @Test
    public void sync_EmptyActualNotes_ShouldNotSaveNotApplicableNewsNotes() {
        SourceEntity src = source("name");
        LanguageEntity en = en();
        ReaderEntity notApplicable = reader(en, Set.of(en), Set.of(src));
        CategoryEntity uk = region("UK", Set.of());
        notApplicable.setCategories(Set.of(uk));

        mockServiceNotes(Set.of());

        newsSynchroniser.sync();

        verify(notificationService, never()).saveNew(eq(notApplicable), any());
    }

    private void mockServiceNotes(Set<NewsNoteEntity> notes) {
        when(newsNoteService.getTodayNotes()).thenReturn(notes);
        when(newsNoteService.getAllAfter(any())).thenReturn(notes);
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