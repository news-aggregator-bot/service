package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.request.CategoryRequest;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.domain.request.ListCategoryRequest;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Reader;
import bepicky.service.entity.SourcePage;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryFunctionalFacade implements ICategoryFunctionalFacade {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private CategoryDtoMapper categoryResponseMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryListResponse listAll(ListCategoryRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:category:reader {} not found", request.getChatId());
            return new CategoryListResponse(ErrorUtil.readerNotFound());
        }
        CategoryType type = CategoryType.valueOf(request.getType());
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(reader, categoryService.findTopCategories(type, req));
    }

    @Override
    public CategoryListResponse listSub(ListCategoryRequest request) {
        Category parent = categoryService.find(request.getParentId()).orElse(null);
        if (parent == null) {
            log.warn("list:subcategory:parent category {} not found", request.getParentId());
            return new CategoryListResponse(ErrorUtil.categoryNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:subcategory:reader {} not found", request.getChatId());
            return new CategoryListResponse(ErrorUtil.readerNotFound());
        }
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(
            reader,
            categoryService.findByParent(parent, req)
        );
    }

    @Override
    public CategoryResponse pick(CategoryRequest request) {
        return doAction(request, (r, c) -> {
            r.addCategory(c);
            r.addSources(c.getSourcePages().stream().map(SourcePage::getSource).collect(Collectors.toSet()));
        });
    }

    @Override
    public CategoryResponse remove(CategoryRequest request) {
        return doAction(request, Reader::removeCategory);
    }

    private CategoryResponse doAction(CategoryRequest request, BiConsumer<Reader, Category> action) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("action:category:reader {} not found", request.getChatId());
            return new CategoryResponse(ErrorUtil.readerNotFound());
        }
        Category category = categoryService.find(request.getCategoryId()).orElse(null);
        if (category == null) {
            log.warn("action:category:category {} not found", request.getCategoryId());
            return new CategoryResponse(ErrorUtil.categoryNotFound());
        }
        action.accept(reader, category);
        readerService.save(reader);
        return new CategoryResponse(
            modelMapper.map(reader, ReaderDto.class),
            categoryResponseMapper.toFullDto(category, reader.getPrimaryLanguage())
        );
    }

    private CategoryListResponse getListCategoryResponse(Reader reader, Page<Category> categoryPage) {
        try {
            List<CategoryDto> dtos = categoryPage
                .stream()
                .filter(c -> matchesReader(reader, c))
                .map(c -> categoryResponseMapper.toFullDto(c, reader.getPrimaryLanguage()))
                .collect(Collectors.toList());

            return new CategoryListResponse(
                dtos,
                categoryPage.isFirst(),
                categoryPage.isLast(),
                modelMapper.map(reader, ReaderDto.class)
            );
        } catch (ResourceNotFoundException e) {
            return new CategoryListResponse(ErrorUtil.languageNotFound());
        }
    }

    private boolean matchesReader(Reader reader, Category c) {
        return c.getSourcePages()
            .stream()
            .anyMatch(sp -> sp.getLanguages().stream().anyMatch(l -> reader.getLanguages().contains(l)));
    }


}
