package bepicky.service.service;

import org.junit.jupiter.api.Disabled;
import picky.test.SingletonMySQLContainerSupport;
import picky.test.NatsContainerSupport;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.TestEntityManager;
import bepicky.service.repository.NewsNoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Disabled
public class NewsNoteServiceTest implements NatsContainerSupport, SingletonMySQLContainerSupport {

    @Autowired
    private INewsNoteService newsNoteService;

    @Autowired
    private NewsNoteRepository newsNoteRepository;

    @Test
    public void archiveEarlierThan_ExistingNotesEarlier_ShouldArchiveOldNotes() {
        Date fourMonthsAgo = TestEntityManager.beforeM(4);
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
        Date fourMonthsAgo = TestEntityManager.beforeM(2);
        NewsNote created = newsNoteRepository.save(TestEntityManager.note(
            "title",
            fourMonthsAgo
        ));

        Set<NewsNote> newsNotes = newsNoteService.archiveEarlierThan(3);

        assertEquals(0, newsNotes.size());
        assertTrue(newsNoteRepository.existsById(created.getId()));
    }

}