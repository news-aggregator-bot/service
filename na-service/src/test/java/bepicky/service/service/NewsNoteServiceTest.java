package bepicky.service.service;

import bepicky.service.YamlPropertySourceFactory;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.TestEntityManager;
import bepicky.service.repository.NewsNoteRepository;
import bepicky.service.service.util.IValueNormalisationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NewsNoteServiceTest {

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private NewsNoteRepository newsNoteRepository;

    @MockBean
    private IValueNormalisationService normalisationService;

    @Test
    public void archiveEarlierThan_ExistingNotesEarlier_ShouldArchiveOldNotes() {
        Date fourMonthsAgo = TestEntityManager.before(4);
        NewsNote created = newsNoteRepository.save(TestEntityManager.note(
            "title",
            fourMonthsAgo
        ));

        Set<NewsNote> newsNotes = newsNoteService.archiveEarlierThan(3);

        assertTrue(newsNotes.contains(created));
        assertFalse(newsNoteRepository.existsById(created.getId()));
    }

    @Test
    public void archiveEarlierThan_NotExistingNotesEarlier_ShouldNotArchiveAnyNotes() {
        Date fourMonthsAgo = TestEntityManager.before(2);
        NewsNote created = newsNoteRepository.save(TestEntityManager.note(
            "title",
            fourMonthsAgo
        ));

        Set<NewsNote> newsNotes = newsNoteService.archiveEarlierThan(3);

        assertEquals(0, newsNotes.size());
        assertTrue(newsNoteRepository.existsById(created.getId()));
    }

    @TestConfiguration
    @EntityScan("bepicky.service.entity")
    @ComponentScan({"bepicky.service.repository"})
    @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yml")
    static class NewsNoteServiceTestConfiguration{

        @Bean
        INewsNoteService newsNoteService() {
            return new NewsNoteService();
        }
    }
}