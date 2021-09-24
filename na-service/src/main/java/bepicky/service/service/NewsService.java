package bepicky.service.service;

import bepicky.service.entity.NewsNoteEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.entity.TagEntity;
import bepicky.service.service.util.IValueNormalisationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import picky.data.reader.dto.ParsedSourcePageDto;
import picky.data.reader.dto.ParsedNewsNoteDto;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class NewsService implements INewsService {

    private static final int MAX_LINK_LENGTH = 251;

    private final INewsNoteService newsNoteService;

    private final ISourcePageService spService;

    private final IValueNormalisationService normalisationService;

    private final ITagService tagService;

    @Value("${na.news.domain-check:true}")
    private boolean checkDomain;

    public NewsService(
        INewsNoteService newsNoteService,
        ISourcePageService spService,
        IValueNormalisationService normalisationService,
        ITagService tagService
    ) {
        this.newsNoteService = newsNoteService;
        this.spService = spService;
        this.normalisationService = normalisationService;
        this.tagService = tagService;
    }

    @Override
    public void handleParsed(ParsedSourcePageDto sourcePage) {
        SourcePageEntity sp = spService.findById(sourcePage.getId())
            .orElseThrow(() -> new IllegalArgumentException("source page doesn't exist " + sourcePage.getId()));
        Set<NewsNoteEntity> freshNews = sourcePage.getPages()
            .stream()
            .filter(d -> validLink(sp, d))
            .map(d -> toNoteEntity(sp, d))
            .collect(Collectors.toSet());
        if (!freshNews.isEmpty()) {
            log.info("news:parsed: {} : {}", sp.getUrl(), freshNews.size());
            newsNoteService.saveAll(freshNews);
        }
    }

    private boolean validLink(SourcePageEntity page, ParsedNewsNoteDto d) {
        if (StringUtils.isBlank(d.getLink())) {
            log.debug("news:skip:empty link:" + d.getLink());
            return false;
        }
        if (d.getLink().length() > MAX_LINK_LENGTH) {
            log.debug("news:skip:long link:" + d.getLink());
            return false;
        }
        if (checkDomain && !d.getLink().contains(page.getHost())) {
            log.debug("news:skip:wrong host:" + d.getLink());
            return false;
        }
        return !newsNoteService.existsByUrl(d.getLink());
    }

    private NewsNoteEntity toNoteEntity(SourcePageEntity page, ParsedNewsNoteDto data) {
        String title = normalisationService.trimTitle(data.getTitle());
        String normTitle = normalisationService.normaliseTitle(title);
        return newsNoteService.findByNormalisedTitle(normTitle)
            .filter(n -> DateUtils.isSameDay(new Date(), n.getCreationDate()))
            .map(n -> {
                n.addSourcePage(page);
                return n;
            }).orElseGet(() -> {
                NewsNoteEntity note = new NewsNoteEntity();
                Set<TagEntity> tagsTitle = tagService.findByTitle(title);
                note.setTitle(title);
                note.setNormalisedTitle(normTitle);
                note.setUrl(data.getLink());
                note.setAuthor(data.getAuthor());
                note.addSourcePage(page);
                note.setTags(tagsTitle);
                return note;
            });
    }
}
