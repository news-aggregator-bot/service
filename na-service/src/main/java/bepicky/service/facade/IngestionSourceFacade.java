package bepicky.service.facade;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.dto.ContentBlockDto;
import bepicky.service.dto.ContentTagDto;
import bepicky.service.dto.Ids;
import bepicky.service.dto.SourceDto;
import bepicky.service.dto.SourcePageDto;
import bepicky.service.entity.CategoryEntity;
import bepicky.service.entity.ContentBlockEntity;
import bepicky.service.entity.ContentTagEntity;
import bepicky.service.entity.LanguageEntity;
import bepicky.service.entity.SourceEntity;
import bepicky.service.entity.SourcePageEntity;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.IContentBlockService;
import bepicky.service.service.IContentTagService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class IngestionSourceFacade {

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private IContentBlockService contentBlockService;

    @Autowired
    private IContentTagService contentTagService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ICategoryService categoryService;

    public SourceEntity ingest(SourceDto srcDto) {
        SourceEntity source = getSource(srcDto);
        Set<String> pages = new HashSet<>();
        for (SourcePageDto pageDto : srcDto.getPages()) {
            SourcePageEntity srcPage = sourcePageService.findByUrl(pageDto.getUrl()).orElseGet(() -> {
                SourcePageEntity srcPg = new SourcePageEntity();
                srcPg.setUrl(pageDto.getUrl());
                return srcPg;
            });

            srcPage.setUrlNormalisation(pageDto.getUrlNormalisation());
            srcPage.setSource(source);
            srcPage.setLanguages(findLanguages(pageDto));
            srcPage.setCategories(findCategories(pageDto));
            SourcePageEntity savedSrcPage = sourcePageService.save(srcPage);

            List<ContentBlockEntity> contentBlocks = pageDto.getBlocks()
                .stream()
                .map(cb -> buildContentBlock(savedSrcPage, cb))
                .collect(Collectors.toList());
            Set<ContentBlockEntity> set = new HashSet<>(contentBlocks);
            if (savedSrcPage.getContentBlocks() != null) {
                Set<ContentBlockEntity> srcPageContentBlocks = savedSrcPage.getContentBlocks();
                savedSrcPage.setContentBlocks(null);
                contentBlockService.deleteAll(srcPageContentBlocks);
            }
            savedSrcPage.setContentBlocks(set);
            pages.add(srcPage.getUrl());
            contentBlockService.saveAll(contentBlocks);
        }
        if (source.getPages() != null) {
            source.getPages().stream()
                .filter(sp -> !pages.contains(sp.getUrl()))
                .forEach(sp -> sourcePageService.disable(sp.getId()));
        }
        return source;
    }

    private SourceEntity getSource(SourceDto srcDto) {
        Optional<SourceEntity> source = sourceService.findByName(srcDto.getName());
        if (source.isPresent()) {
            return source.get();
        }
        SourceEntity newSrc = new SourceEntity();
        newSrc.setName(srcDto.getName());
        return sourceService.save(newSrc);
    }

    private ContentBlockEntity buildContentBlock(SourcePageEntity page, ContentBlockDto dto) {
        Set<ContentTagEntity> contentTags = dto.getContentTags()
            .stream()
            .map(this::findContentTag)
            .collect(Collectors.toSet());
        contentTagService.saveAll(contentTags);

        ContentBlockEntity block = new ContentBlockEntity();
        block.setSourcePage(page);
        block.setTags(contentTags);
        return block;
    }

    private ContentTagEntity findContentTag(ContentTagDto tag) {
        return contentTagService.findByValue(tag.getValue())
            .stream()
            .filter(t -> t.getType() == tag.getType() && t.getMatchStrategy() == tag.getMatchStrategy())
            .findFirst()
            .orElseGet(() -> {
                ContentTagEntity contentTag = new ContentTagEntity();
                contentTag.setType(tag.getType());
                contentTag.setValue(tag.getValue());
                contentTag.setMatchStrategy(tag.getMatchStrategy());
                return contentTag;
            });
    }

    private Set<LanguageEntity> findLanguages(SourcePageDto pageDto) {
        return pageDto.getLanguages().stream().map(this::findLanguage).collect(Collectors.toSet());
    }

    private LanguageEntity findLanguage(String l) {
        return languageService.find(l).orElseThrow(() -> new ResourceNotFoundException(l + " language not found."));
    }

    private List<CategoryEntity> findCategories(SourcePageDto pageDto) {
        try {
            return pageDto.getCategories()
                .stream()
                .map(this::getCategory)
                .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(pageDto.getName() + " - " + pageDto.getUrl() + " ingestion failed: " + e.getMessage());
        }
    }

    private CategoryEntity getCategory(String c) {
        return categoryService.findByName(c.trim())
            .orElseThrow(() -> new ResourceNotFoundException(c + " category not found."));
    }
}
