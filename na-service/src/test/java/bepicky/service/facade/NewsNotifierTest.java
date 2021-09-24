package bepicky.service.facade;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.NewsNoteNotificationDto;
import bepicky.common.domain.dto.SourcePageDto;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.configuration.WebConfiguration;
import bepicky.service.dto.mapper.CategoryDtoMapper;
import bepicky.service.dto.mapper.NewsNoteDtoMapper;
import bepicky.service.dto.mapper.SourcePageDtoMapper;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.LocalisationEntity;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.NewsNoteNotificationEntity;
import bepicky.service.entity.ReaderEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.nats.publisher.NewsNotificationPublisher;
import bepicky.service.schedule.NewsNotifier;
import bepicky.service.service.INewsNoteNotificationService;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewsNotifierTest {

    private static final String TEST_URL = "url";
    private static final String TEST_TITLE = "title";
    private static final String TEST_AUTHOR = "author";
    private static final String TEST_USA = "USA";

    @Autowired
    private NewsNotifier newsNotifier;

    @MockBean
    private IReaderService readerService;

    @MockBean
    private INewsNoteNotificationService notificationService;

    @MockBean
    private NewsNotificationPublisher publisher;

    @Test
    public void sync_FullNewsNote_ShouldSendCorrectNotifyNewsRequest() {

        LanguageEntity language = defaultLanguage();
        LocalisationEntity usaLocalisation = defaultLocalisation(language);
        CategoryEntity regionUSA = regionCategory(usaLocalisation);
        SourcePageEntity sourcePage = sourcePage(Sets.newHashSet(language));

        sourcePage.setCategories(Arrays.asList(regionUSA));
        regionUSA.setSourcePages(Arrays.asList(sourcePage));

        NewsNoteEntity note = newsNote(TEST_TITLE, TEST_URL, TEST_AUTHOR, sourcePage);
        ReaderEntity r = reader(1L, language);
        NewsNoteNotificationEntity notification = newNoteNotification(note, r);

        ArgumentCaptor<NewsNoteNotificationDto> notifyNewsAc = ArgumentCaptor.forClass(NewsNoteNotificationDto.class);

        when(readerService.findAllEnabled()).thenReturn(Arrays.asList(r));
        when(notificationService.findAllNew(eq(r))).thenReturn(Arrays.asList(notification));

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

        LanguageEntity language = defaultLanguage();
        LocalisationEntity usaLocalisation = defaultLocalisation(language);
        CategoryEntity regionUSA = regionCategory(usaLocalisation);
        SourcePageEntity sourcePage = sourcePage(Sets.newHashSet(language));

        sourcePage.setCategories(Arrays.asList(regionUSA));
        regionUSA.setSourcePages(Arrays.asList(sourcePage));

        NewsNoteEntity note = newsNote(TEST_TITLE, TEST_URL, TEST_AUTHOR, sourcePage);
        ReaderEntity r = reader(1L, language);
        NewsNoteNotificationEntity notification = newNoteNotification(note, r, NewsNoteNotificationEntity.Link.TAG, "key");

        ArgumentCaptor<NewsNoteNotificationDto> notifyNewsAc = ArgumentCaptor.forClass(NewsNoteNotificationDto.class);

        when(readerService.findAllEnabled()).thenReturn(Arrays.asList(r));
        when(notificationService.findAllNew(eq(r))).thenReturn(Arrays.asList(notification));

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

    private NewsNoteNotificationEntity newNoteNotification(NewsNoteEntity note, ReaderEntity r) {
        NewsNoteNotificationEntity nnn = new NewsNoteNotificationEntity(r, note);
        nnn.setLink(NewsNoteNotificationEntity.Link.CATEGORY);
        return nnn;
    }

    private NewsNoteNotificationEntity newNoteNotification(NewsNoteEntity note, ReaderEntity r, NewsNoteNotificationEntity.Link link, String key) {
        NewsNoteNotificationEntity nnn = new NewsNoteNotificationEntity(r, note);
        nnn.setLink(link);
        nnn.setLinkKey(key);
        return nnn;
    }

    private ReaderEntity reader(Long id, LanguageEntity language) {
        ReaderEntity r = new ReaderEntity();
        r.setId(System.currentTimeMillis());
        r.setStatus(ReaderEntity.Status.ENABLED);
        r.setChatId(id);
        r.setPrimaryLanguage(language);
        return r;
    }

    private NewsNoteEntity newsNote(String title, String url, String author, SourcePageEntity sourcePage) {
        NewsNoteEntity note = new NewsNoteEntity();
        note.setId(System.currentTimeMillis());
        note.setUrl(url);
        note.setTitle(title);
        note.setAuthor(author);
        note.addSourcePage(sourcePage);
        return note;
    }

    private SourcePageEntity sourcePage(Set<LanguageEntity> languages) {
        SourceEntity src = new SourceEntity();
        src.setName("name");
        SourcePageEntity sourcePage = new SourcePageEntity();
        sourcePage.setUrl(TEST_URL);
        sourcePage.setLanguages(languages);
        sourcePage.setSource(src);
        return sourcePage;
    }

    private CategoryEntity regionCategory(LocalisationEntity usaLocalisation) {
        CategoryEntity regionUSA = new CategoryEntity();
        regionUSA.setId(1L);
        regionUSA.setType(CategoryType.REGION);
        regionUSA.setName(TEST_USA);
        regionUSA.setLocalisations(Arrays.asList(usaLocalisation));
        return regionUSA;
    }

    private LocalisationEntity defaultLocalisation(LanguageEntity language) {
        LocalisationEntity usaLocalisation = new LocalisationEntity();
        usaLocalisation.setValue(TEST_USA);
        usaLocalisation.setLanguage(language);
        return usaLocalisation;
    }

    private LanguageEntity defaultLanguage() {
        LanguageEntity language = new LanguageEntity();
        language.setLang("en");
        return language;
    }


    @TestConfiguration
    @Import({WebConfiguration.class, SourcePageDtoMapper.class, NewsNoteDtoMapper.class, CategoryDtoMapper.class})
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-it.yml")
    static class NewsNotifierTestConfiguration {

        @Bean
        public NewsNotifier newsNotifier() {
            return new NewsNotifier();
        }

    }
}