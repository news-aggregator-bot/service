package bepicky.service.facade;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.request.NewsNoteRequest;
import bepicky.common.domain.request.NotifyNewsRequest;
import bepicky.common.domain.request.SourcePageRequest;
import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.client.NaBotClient;
import bepicky.service.configuration.WebConfiguration;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.domain.mapper.NewsNoteDtoMapper;
import bepicky.service.domain.mapper.SourcePageDtoMapper;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryLocalisation;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Language;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.IReaderService;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class NewsNotifierTest {

    private static final String TEST_URL = "url";
    private static final String TEST_TITLE = "title";
    private static final String TEST_AUTHOR = "author";
    private static final String TEST_USA = "USA";

    @Autowired
    private NewsNotifier newsNotifier;

    @MockBean
    private NaBotClient botClient;

    @MockBean
    private IReaderService readerService;

    @Test
    public void sync_FullNewsNote_ShouldSendCorrectNotifyNewsRequest() {

        Language language = defaultLanguage();
        CategoryLocalisation usaLocalisation = defaultLocalisation(language);
        Category regionUSA = regionCategory(usaLocalisation);
        SourcePage sourcePage = sourcePage(Sets.newHashSet(language));

        sourcePage.setCategories(Arrays.asList(regionUSA));
        regionUSA.setSourcePages(Arrays.asList(sourcePage));

        NewsNote note = newsNote(TEST_TITLE, TEST_URL, TEST_AUTHOR, sourcePage);
        Reader r = reader(1L, language, note);

        ArgumentCaptor<NotifyNewsRequest> notifyNewsAc = ArgumentCaptor.forClass(NotifyNewsRequest.class);

        when(readerService.findAllEnabled()).thenReturn(Arrays.asList(r));

        newsNotifier.sync();


        verify(botClient).notifyNews(notifyNewsAc.capture());

        NotifyNewsRequest actualRequest = notifyNewsAc.getValue();

        assertEquals(1L, actualRequest.getChatId());
        assertEquals(language.getLang(), actualRequest.getLang());

        assertEquals(1, actualRequest.getNotes().size());
        NewsNoteRequest actualNewsNoteRequest = actualRequest.getNotes().get(0);
        assertEquals(note.getUrl(), actualNewsNoteRequest.getUrl());
        assertEquals(note.getTitle(), actualNewsNoteRequest.getTitle());
        assertEquals(note.getAuthor(), actualNewsNoteRequest.getAuthor());

        SourcePageRequest actualSp = actualNewsNoteRequest.getSourcePage();
        assertEquals(sourcePage.getUrl(), actualSp.getUrl());
        assertEquals(sourcePage.getName(), actualSp.getName());

        assertEquals(1, actualSp.getCategories().size());
        CategoryDto actualSpCategory = actualSp.getCategories().get(0);

        assertEquals(regionUSA.getName(), actualSpCategory.getName());
        assertEquals(regionUSA.getType().name(), actualSpCategory.getType());
        assertEquals(usaLocalisation.getValue(), actualSpCategory.getLocalised());
    }

    private Reader reader(Long id, Language language, NewsNote note) {
        Reader r = new Reader();
        r.setStatus(Reader.Status.ENABLED);
        r.setChatId(id);
        r.setPrimaryLanguage(language);
        r.setNotifyQueue(Sets.newHashSet(note));
        return r;
    }

    private NewsNote newsNote(String title, String url, String author, SourcePage sourcePage) {
        NewsNote note = new NewsNote();
        note.setUrl(url);
        note.setTitle(title);
        note.setAuthor(author);
        note.setSourcePage(sourcePage);
        return note;
    }

    private SourcePage sourcePage(Set<Language> languages) {
        SourcePage sourcePage = new SourcePage();
        sourcePage.setUrl(TEST_URL);
        sourcePage.setName(TEST_USA);
        sourcePage.setLanguages(languages);
        return sourcePage;
    }

    private Category regionCategory(CategoryLocalisation usaLocalisation) {
        Category regionUSA = new Category();
        regionUSA.setId(1L);
        regionUSA.setType(CategoryType.REGION);
        regionUSA.setName(TEST_USA);
        regionUSA.setLocalisations(Arrays.asList(usaLocalisation));
        return regionUSA;
    }

    private CategoryLocalisation defaultLocalisation(Language language) {
        CategoryLocalisation usaLocalisation = new CategoryLocalisation();
        usaLocalisation.setValue(TEST_USA);
        usaLocalisation.setLanguage(language);
        return usaLocalisation;
    }

    private Language defaultLanguage() {
        Language language = new Language();
        language.setLang("en");
        return language;
    }


    @TestConfiguration
    @Import({WebConfiguration.class, SourcePageDtoMapper.class, NewsNoteDtoMapper.class, CategoryDtoMapper.class})
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yml")
    static class NewsNotifierTestConfiguration {

        @Bean
        public NewsNotifier newsNotifier() {
            return new NewsNotifier();
        }

    }
}