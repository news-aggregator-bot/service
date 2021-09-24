package bepicky.service.service;

import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.repository.NewsNoteNativeRepository;
import bepicky.service.repository.NewsNoteRepository;
import bepicky.service.service.util.IValueNormalisationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@Slf4j
@Transactional
public class NewsNoteService implements INewsNoteService {

    @Autowired
    private NewsNoteRepository repository;

    @Autowired
    private NewsNoteNativeRepository newsNoteNativeRepository;

    @Autowired
    private IValueNormalisationService normalisationService;

    @Override
    public NewsNoteEntity save(NewsNoteEntity note) {
        log.info("news:save:{}", note);
        return repository.saveAndFlush(note);
    }

    @Override
    public Collection<NewsNoteEntity> saveAll(Collection<NewsNoteEntity> notes) {
        log.info("news:save:{}", notes);
        return repository.saveAll(notes);
    }

    @Override
    public Optional<NewsNoteEntity> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<NewsNoteEntity> findAllByNormalisedTitle(String title) {
        return repository.findAllByNormalisedTitle(title);
    }

    @Override
    public Optional<NewsNoteEntity> findByNormalisedTitle(String title) {
        return repository.findTopByNormalisedTitleOrderByIdDesc(title);
    }

    @Override
    public Set<NewsNoteEntity> getAllAfter(Long id) {
        return repository.findByIdGreaterThan(id);
    }

    @Override
    public Set<NewsNoteEntity> getNotesBetween(Date from, Date to) {
        return repository.findByCreationDateBetween(from, to);
    }

    @Override
    public Set<NewsNoteEntity> getTodayNotes() {
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
    public Set<NewsNoteEntity> archiveEarlierThan(int months) {
        Calendar fewMonthsAgo = new GregorianCalendar();
        fewMonthsAgo.add(Calendar.MONTH, -months);
        Set<NewsNoteEntity> oldNews = repository.findByCreationDateBefore(fewMonthsAgo.getTime());
        log.info("news_note:archive:{}", oldNews.size());
        repository.deleteAll(oldNews);
        return oldNews;
    }

    @Override
    public boolean existsByUrl(String url) {
        return repository.existsByUrl(url);
    }

    @Override
    public boolean existsByNormalisedTitle(String normalisedTitle) {
        return repository.existsByNormalisedTitle(normalisedTitle);
    }

    @Override
    public Page<NewsNoteEntity> searchByTitle(String key, Pageable pageable) {
        String normalisedKey = normalisationService.normaliseTitle(key);
        if (StringUtils.isBlank(normalisedKey) || normalisedKey.length() < 2) {
            return Page.empty();
        }
        Set<String> keys = Set.of(normalisedKey.split(" "));
        if (keys.size() < 2) {
            return repository.findByNormalisedTitleContainsOrderByCreationDateDesc(normalisedKey, pageable);
        }
        List<Long> noteIds = newsNoteNativeRepository.find(keys);
        if (noteIds.isEmpty()) {
            return Page.empty();
        }

        List<Long> pagedNoteIds = Lists.partition(noteIds, pageable.getPageSize())
            .get(pageable.getPageNumber());
        List<NewsNoteEntity> notes = repository.findAllById(pagedNoteIds);
        return new PageImpl<>(notes, pageable, noteIds.size());
    }

    @Override
    public List<NewsNoteEntity> refresh(long from, long to) {
        List<Long> ids = LongStream.rangeClosed(from, to).boxed().collect(Collectors.toList());
        log.info("news_note:refresh:id from {} to {}", from, to);
        return repository.saveAll(repository.findAllByIdIn(ids));
    }

}
