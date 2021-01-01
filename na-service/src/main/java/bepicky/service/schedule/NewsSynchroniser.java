package bepicky.service.schedule;

import bepicky.service.entity.Category;
import bepicky.service.entity.NewsNote;
import bepicky.service.entity.Reader;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.INewsNoteService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class NewsSynchroniser {

    @Autowired
    private INewsNoteService notesService;

    @Autowired
    private IReaderService readerService;

    @Value("${na.schedule.sync.enabled}")
    private boolean syncEnabled;

    private long latestNewsNoteId = 0;

    @Transactional
    @Scheduled(cron = "${na.schedule.sync.cron:0 * * * * *}")
    public void sync() {
        if (!syncEnabled) {
            return;
        }
        Set<NewsNote> actualNotes = latestNewsNoteId != 0 ?
            notesService.getAllAfter(latestNewsNoteId) :
            notesService.getTodayNotes();

        if (actualNotes.isEmpty()) {
            log.debug("news:sync:finished:empty");
            return;
        }
        log.info("news:sync:get_fresh:{}", actualNotes.size());
        Set<Reader> updatedReaders = actualNotes.stream()
            .collect(Collectors.groupingBy(NewsNote::getSourcePage, Collectors.toSet()))
            .entrySet()
            .stream()
            .flatMap(e -> findApplicableReaders(e.getKey()).peek(r -> r.addQueueNewsNote(e.getValue())))
            .collect(Collectors.toSet());
        readerService.saveAll(updatedReaders);
        latestNewsNoteId = actualNotes.stream().mapToLong(NewsNote::getId).max().orElseGet(() -> latestNewsNoteId);
    }

    private Stream<Reader> findApplicableReaders(SourcePage sp) {
        if (sp.getRegions() == null) {
            return filterReaders(sp, sp.getCategories());
        }
        return filterReaders(sp, sp.getRegions())
            .filter(r -> atLeastOneInCommon(sp.getCommon(), r.getCategories()));
    }

    private Stream<Reader> filterReaders(SourcePage sp, Collection<Category> categories) {
        return categories
            .stream()
            .map(Category::getReaders)
            .flatMap(Set::stream)
            .filter(Reader::isEnabled)
            .filter(r -> atLeastOneInCommon(sp.getLanguages(), r.getLanguages()))
            .filter(r -> r.getSources().contains(sp.getSource()));
    }

    private <T> boolean atLeastOneInCommon(Collection<T> c1, Collection<T> c2) {
        return c1.stream().anyMatch(c2::contains);
    }

}
