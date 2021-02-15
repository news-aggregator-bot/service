package bepicky.service.facade;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.dto.ContentBlockDto;
import bepicky.service.domain.dto.ContentTagDto;
import bepicky.service.domain.dto.SourceDto;
import bepicky.service.domain.dto.SourcePageDto;
import bepicky.service.entity.Category;
import bepicky.service.entity.ContentBlock;
import bepicky.service.entity.ContentTag;
import bepicky.service.entity.Language;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.IContentBlockService;
import bepicky.service.service.IContentTagService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public Source ingest(SourceDto srcDto) {
        Source source = getSource(srcDto);
        for (SourcePageDto pageDto : srcDto.getPages()) {
            SourcePage srcPage = sourcePageService.findByUrl(pageDto.getUrl()).orElseGet(() -> {
                SourcePage srcPg = new SourcePage();
                srcPg.setUrl(pageDto.getUrl());
                srcPg.setName(pageDto.getName());
                return srcPg;
            });

            srcPage.setUrlNormalisation(pageDto.getUrlNormalisation());
            srcPage.setSource(source);
            srcPage.setLanguages(findLanguages(pageDto));
            srcPage.setCategories(findCategories(pageDto));
            SourcePage savedSrcPage = sourcePageService.save(srcPage);

            List<ContentBlock> contentBlocks = pageDto.getBlocks()
                .stream()
                .map(cb -> buildContentBlock(savedSrcPage, cb))
                .collect(Collectors.toList());
            Set<ContentBlock> set = contentBlocks.stream().collect(Collectors.toSet());
            if (savedSrcPage.getContentBlocks() != null) {
                Set<ContentBlock> srcPageContentBlocks = savedSrcPage.getContentBlocks();
                savedSrcPage.setContentBlocks(null);
                contentBlockService.deleteAll(srcPageContentBlocks);
            }
            savedSrcPage.setContentBlocks(set);

            contentBlockService.saveAll(contentBlocks);
        }

        return source;
    }

    private Source getSource(SourceDto srcDto) {
        Optional<Source> source = sourceService.findByName(srcDto.getName());
        if (source.isPresent()) {
            return source.get();
        }
        Source newSrc = new Source();
        newSrc.setName(srcDto.getName());
        return sourceService.save(newSrc);
    }

    private ContentBlock buildContentBlock(SourcePage page, ContentBlockDto dto) {
        Set<ContentTag> contentTags = dto.getContentTags()
            .stream()
            .map(this::findContentTag)
            .collect(Collectors.toSet());
        contentTagService.saveAll(contentTags);

        ContentBlock block = new ContentBlock();
        block.setSourcePage(page);
        block.setTags(contentTags);
        return block;
    }

    private ContentTag findContentTag(ContentTagDto tag) {
        return contentTagService.findByValue(tag.getValue())
            .stream()
            .filter(t -> t.getType() == tag.getType() && t.getMatchStrategy() == tag.getMatchStrategy())
            .findFirst()
            .orElseGet(() -> {
                ContentTag contentTag = new ContentTag();
                contentTag.setType(tag.getType());
                contentTag.setValue(tag.getValue());
                contentTag.setMatchStrategy(tag.getMatchStrategy());
                return contentTag;
            });
    }

    private Set<Language> findLanguages(SourcePageDto pageDto) {
        return pageDto.getLanguages().stream().map(this::findLanguage).collect(Collectors.toSet());
    }

    private Language findLanguage(String l) {
        return languageService.find(l).orElseThrow(() -> new ResourceNotFoundException(l + " language not found."));
    }

    private List<Category> findCategories(SourcePageDto pageDto) {
        try {
            return pageDto.getCategories()
                .stream()
                .map(this::getCategory)
                .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(pageDto.getName() + " - " + pageDto.getUrl() + " ingestion failed: " + e.getMessage());
        }
    }

    private Category getCategory(String c) {
        return categoryService.findByName(c.trim())
            .orElseThrow(() -> new ResourceNotFoundException(c + " category not found."));
    }
}
