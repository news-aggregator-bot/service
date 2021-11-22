package bepicky.service.service;

import bepicky.service.entity.NewsNote;
import bepicky.service.repository.NewsNoteNativeRepository;
import bepicky.service.repository.NewsNoteRepository;
import bepicky.service.service.util.IValueNormalisationService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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

    private final NewsNoteRepository repository;
    private final NewsNoteNativeRepository newsNoteNativeRepository;
    private final IValueNormalisationService normalisationService;

    private final ListMultimap<String, NewsNote> inMemoryBucket =
        Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public NewsNoteService(
        NewsNoteRepository repository,
        NewsNoteNativeRepository newsNoteNativeRepository,
        IValueNormalisationService normalisationService
    ) {
        this.repository = repository;
        this.newsNoteNativeRepository = newsNoteNativeRepository;
        this.normalisationService = normalisationService;
    }

    @Override
    public NewsNote save(NewsNote note) {
        log.info("news:save:{}", note);
        return repository.saveAndFlush(note);
    }

    @Override
    public Collection<NewsNote> saveAll(Collection<NewsNote> notes) {
        if (notes.isEmpty()) {
            return Collections.emptyList();
        }
        notes.forEach(n -> inMemoryBucket.put(n.getUrl(), n));
        return notes;
    }

    @Override
    public Optional<NewsNote> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<NewsNote> findAllByNormalisedTitle(String title) {
        return repository.findAllByNormalisedTitle(title);
    }

    @Override
    public Optional<NewsNote> findByNormalisedTitle(String title) {
        return repository.findTopByNormalisedTitleOrderByIdDesc(title);
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
    public Set<NewsNote> archiveEarlierThan(int months) {
        Calendar fewMonthsAgo = new GregorianCalendar();
        fewMonthsAgo.add(Calendar.MONTH, -months);
        Set<NewsNote> oldNews = repository.findByCreationDateBefore(fewMonthsAgo.getTime());
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
    public Page<NewsNote> searchByTitle(String key, Pageable pageable) {
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
        List<NewsNote> notes = repository.findAllById(pagedNoteIds);
        return new PageImpl<>(notes, pageable, noteIds.size());
    }

    @Override
    public List<NewsNote> refresh(long from, long to) {
        List<Long> ids = LongStream.rangeClosed(from, to).boxed().collect(Collectors.toList());
        log.info("news_note:refresh:id from {} to {}", from, to);
        return repository.saveAll(repository.findAllByIdIn(ids));
    }

    @Override
    public Collection<NewsNote> flush() {
        List<NewsNote> freshArticles = inMemoryBucket.asMap().values()
            .stream().map(notes -> notes.stream()
                .reduce((n1, n2) -> {
                    n2.getSourcePages().forEach(n1::addSourcePage);
                    return n1;
                })).map(Optional::get)
            .collect(Collectors.toList());
        log.info("news:save {} :{}", freshArticles.size(), freshArticles);
        inMemoryBucket.clear();
        return repository.saveAll(freshArticles);
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 2 * 60 * 1000)
    public void scheduleFlush() {
        flush();
    }

}
