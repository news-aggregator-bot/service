package bepicky.service.facade;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.common.domain.dto.SourcePageDto;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.domain.mapper.SourcePageDtoMapper;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Language;
import bepicky.service.entity.Localisation;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.NewsNoteNotification;
import bepicky.service.entity.Reader;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.nats.publisher.NewsNotificationPublisher;
import bepicky.service.schedule.NewsNotifier;
import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsNotifierTest {

    private static final String TEST_URL = "url";
    private static final String TEST_TITLE = "title";
    private static final String TEST_AUTHOR = "author";
    private static final String TEST_USA = "USA";

    @InjectMocks
    private NewsNotifier newsNotifier;

    @Mock
    private IReaderService readerService;

    @Mock
    private INewsNoteNotificationService notificationService;

    @Mock
    private NewsNotificationPublisher publisher;

    @BeforeEach
    public void enableNotify() {
        ReflectionTestUtils.setField(newsNotifier, "notifyEnabled", true);
        ReflectionTestUtils.setField(newsNotifier, "notifyLimit", 5);
    }

    @Test
    public void sync_FullNewsNote_ShouldSendCorrectNotifyNewsRequest() {

        Language language = defaultLanguage();
        Localisation usaLocalisation = defaultLocalisation(language);
        Category regionUSA = regionCategory(usaLocalisation);
        SourcePage sourcePage = sourcePage(Sets.newHashSet(language));

        sourcePage.setCategories(List.of(regionUSA));
        regionUSA.setSourcePages(List.of(sourcePage));

        NewsNote note = newsNote(TEST_TITLE, TEST_URL, TEST_AUTHOR, sourcePage);
        Reader r = reader(1L, language);
        NewsNoteNotification notification = newNoteNotification(note, r);

        ArgumentCaptor<NewsNoteNotificationDto> notifyNewsAc = ArgumentCaptor.forClass(NewsNoteNotificationDto.class);

        when(readerService.findAllEnabled()).thenReturn(List.of(r));
        when(notificationService.findAllNew(eq(r))).thenReturn(List.of(notification));


        newsNotifier.sync();

        verify(publisher).sendNotification(eq(1L), eq(language.getLang()), notifyNewsAc.capture());

        NewsNoteNotificationDto actualNotification = notifyNewsAc.getValue();
        assertEquals(note.getUrl(), actualNotification.getUrl());
        assertEquals(note.getTitle(), actualNotification.getTitle());
        assertEquals(note.getAuthor(), actualNotification.getAuthor());
        assertEquals(NewsNoteNotificationDto.LinkDto.CATEGORY, actualNotification.getLink());
        assertNull(actualNotification.getLinkKey());

        SourcePageDto actualSp = actualNotification.getSourcePages().get(0);
        assertEquals(sourcePage.getUrl(), actualSp.getUrl());

        assertEquals(1, actualSp.getCategories().size());
        CategoryDto actualSpCategory = actualSp.getCategories().get(0);

        assertEquals(regionUSA.getName(), actualSpCategory.getName());
        assertEquals(regionUSA.getType().name(), actualSpCategory.getType());
        assertEquals(usaLocalisation.getValue(), actualSpCategory.getLocalised());
    }

    @Test
    public void sync_TagNewsNoteNotification_ShouldSendCorrectNotifyNewsRequest() {

        Language language = defaultLanguage();
        Localisation usaLocalisation = defaultLocalisation(language);
        Category regionUSA = regionCategory(usaLocalisation);
        SourcePage sourcePage = sourcePage(Sets.newHashSet(language));

        sourcePage.setCategories(List.of(regionUSA));
        regionUSA.setSourcePages(List.of(sourcePage));

        NewsNote note = newsNote(TEST_TITLE, TEST_URL, TEST_AUTHOR, sourcePage);
        Reader r = reader(1L, language);
        NewsNoteNotification notification = newNoteNotification(note, r, NewsNoteNotification.Link.TAG, "key");

        ArgumentCaptor<NewsNoteNotificationDto> notifyNewsAc = ArgumentCaptor.forClass(NewsNoteNotificationDto.class);

        when(readerService.findAllEnabled()).thenReturn(List.of(r));
        when(notificationService.findAllNew(eq(r))).thenReturn(List.of(notification));

        
        newsNotifier.sync();


        verify(publisher).sendNotification(eq(1L), eq(language.getLang()), notifyNewsAc.capture());

        NewsNoteNotificationDto actualNotification = notifyNewsAc.getValue();
        assertEquals(note.getUrl(), actualNotification.getUrl());
        assertEquals(note.getTitle(), actualNotification.getTitle());
        assertEquals(note.getAuthor(), actualNotification.getAuthor());
        assertEquals(NewsNoteNotificationDto.LinkDto.TAG, actualNotification.getLink());
        assertEquals("key", actualNotification.getLinkKey());

        SourcePageDto actualSp = actualNotification.getSourcePages().get(0);
        assertEquals(sourcePage.getUrl(), actualSp.getUrl());

        assertEquals(1, actualSp.getCategories().size());
        CategoryDto actualSpCategory = actualSp.getCategories().get(0);

        assertEquals(regionUSA.getName(), actualSpCategory.getName());
        assertEquals(regionUSA.getType().name(), actualSpCategory.getType());
        assertEquals(usaLocalisation.getValue(), actualSpCategory.getLocalised());
    }

    private NewsNoteNotification newNoteNotification(NewsNote note, Reader r) {
        NewsNoteNotification nnn = new NewsNoteNotification(r, note);
        nnn.setLink(NewsNoteNotification.Link.CATEGORY);
        return nnn;
    }

    private NewsNoteNotification newNoteNotification(
        NewsNote note,
        Reader r,
        NewsNoteNotification.Link link,
        String key
    ) {
        NewsNoteNotification nnn = new NewsNoteNotification(r, note);
        nnn.setLink(link);
        nnn.setLinkKey(key);
        return nnn;
    }

    private Reader reader(Long id, Language language) {
        Reader r = new Reader();
        r.setId(System.currentTimeMillis());
        r.setStatus(Reader.Status.ENABLED);
        r.setChatId(id);
        r.setPrimaryLanguage(language);
        return r;
    }

    private NewsNote newsNote(String title, String url, String author, SourcePage sourcePage) {
        NewsNote note = new NewsNote();
        note.setId(System.currentTimeMillis());
        note.setUrl(url);
        note.setTitle(title);
        note.setAuthor(author);
        note.addSourcePage(sourcePage);
        return note;
    }

    private SourcePage sourcePage(Set<Language> languages) {
        Source src = new Source();
        src.setName("name");
        SourcePage sourcePage = new SourcePage();
        sourcePage.setUrl(TEST_URL);
        sourcePage.setLanguages(languages);
        sourcePage.setSource(src);
        return sourcePage;
    }

    private Category regionCategory(Localisation usaLocalisation) {
        Category regionUSA = new Category();
        regionUSA.setId(1L);
        regionUSA.setType(CategoryType.REGION);
        regionUSA.setName(TEST_USA);
        regionUSA.setLocalisations(List.of(usaLocalisation));
        return regionUSA;
    }

    private Localisation defaultLocalisation(Language language) {
        Localisation usaLocalisation = new Localisation();
        usaLocalisation.setValue(TEST_USA);
        usaLocalisation.setLanguage(language);
        return usaLocalisation;
    }

    private Language defaultLanguage() {
        Language language = new Language();
        language.setLang("en");
        return language;
    }

}