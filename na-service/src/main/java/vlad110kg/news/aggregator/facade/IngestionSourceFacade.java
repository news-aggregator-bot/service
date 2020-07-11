package vlad110kg.news.aggregator.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.domain.dto.ContentBlockDto;
import vlad110kg.news.aggregator.domain.dto.ContentTagDto;
import vlad110kg.news.aggregator.domain.dto.SourceDto;
import vlad110kg.news.aggregator.domain.dto.SourcePageDto;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.ContentBlock;
import vlad110kg.news.aggregator.entity.ContentTag;
import vlad110kg.news.aggregator.entity.Language;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;
import vlad110kg.news.aggregator.service.ICategoryService;
import vlad110kg.news.aggregator.service.IContentBlockService;
import vlad110kg.news.aggregator.service.IContentTagService;
import vlad110kg.news.aggregator.service.ILanguageService;
import vlad110kg.news.aggregator.service.ISourcePageService;
import vlad110kg.news.aggregator.service.ISourceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
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

            srcPage.setSource(source);
            srcPage.setLanguage(findLanguage(pageDto));
            srcPage.setCategories(findCategories(pageDto));
            SourcePage savedSrcPage = sourcePageService.save(srcPage);

            List<ContentBlock> contentBlocks = pageDto.getBlocks()
                .stream()
                .map(cb -> buildContentBlock(savedSrcPage, cb))
                .collect(Collectors.toList());
            if (savedSrcPage.getContentBlocks() != null) {
                List<ContentBlock> srcPageContentBlocks = savedSrcPage.getContentBlocks();
                savedSrcPage.setContentBlocks(null);
                contentBlockService.deleteAll(srcPageContentBlocks);
            }
            savedSrcPage.setContentBlocks(contentBlocks);

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
        List<ContentTag> contentTags = dto.getContentTags()
            .stream()
            .map(this::findContentTag)
            .collect(Collectors.toList());
        contentTagService.saveAll(contentTags);

        ContentBlock block = new ContentBlock();
        block.setSourcePage(page);
        block.setTags(contentTags);
        return block;
    }

    private ContentTag findContentTag(ContentTagDto tag) {
        return contentTagService.findByValue(tag.getValue())
            .stream()
            .filter(t -> t.getType() == tag.getType())
            .findFirst()
            .orElseGet(() -> {
                ContentTag contentTag = new ContentTag();
                contentTag.setType(tag.getType());
                contentTag.setValue(tag.getValue());
                return contentTag;
            });
    }

    private Language findLanguage(SourcePageDto pageDto) {
        return languageService.find(pageDto.getLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(pageDto.getLanguage() + " language not found."));
    }

    private List<Category> findCategories(SourcePageDto pageDto) {
        return pageDto.getCategories()
            .stream()
            .map(this::getCategory)
            .collect(Collectors.toList());
    }

    private Category getCategory(String c) {
        return categoryService.findByName(c.trim())
            .orElseThrow(() -> new ResourceNotFoundException(c + " category not found."));
    }
}
