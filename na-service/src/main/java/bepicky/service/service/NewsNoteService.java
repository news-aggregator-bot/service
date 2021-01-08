package bepicky.service.service;

import bepicky.service.entity.NewsNote;
import bepicky.service.repository.NewsNoteRepository;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class NewsNoteService implements INewsNoteService {

    @Autowired
    private NewsNoteRepository repository;

    @Autowired
    private IValueNormalisationService normalisationService;

    @Override
    @Transactional
    public NewsNote save(NewsNote note) {
        note.setNormalisedTitle(normalisationService.normaliseTitle(note.getTitle()));
        log.info("news:save:{}", note);
        return repository.saveAndFlush(note);
    }

    @Override
    public Collection<NewsNote> saveAll(Collection<NewsNote> notes) {
        notes.forEach(n -> n.setNormalisedTitle(normalisationService.normaliseTitle(n.getTitle())));
        log.info("news:save:{}", notes);
        return repository.saveAll(notes);
    }

    @Override
    public Optional<NewsNote> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<NewsNote> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    @Override
    public Set<NewsNote> getAllAfter(Long id) {
        return repository.findByIdGreaterThan(id);
    }

    @Override
    public Set<NewsNote> getNotesBetween(Date from, Date to) {
        return repository.findByCreationDateBetween(from, to);
    }

    @Override
    public Set<NewsNote> getTodayNotes() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayMidnight = today.getTime();

        today.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrowMidnight = today.getTime();
        return getNotesBetween(todayMidnight, tomorrowMidnight);
    }

    @Override
    public boolean exists(String url) {
        return repository.existsByUrl(url);
    }

    @Override
    public Page<NewsNote> searchByTitle(String key, Pageable pageable) {
        String normalisedKey = normalisationService.normaliseTitle(key);
        if (StringUtils.isBlank(normalisedKey) || normalisedKey.length() < 2) {
            return Page.empty();
        }
        return repository.findByNormalisedTitleContains(normalisedKey, pageable);
    }

}
