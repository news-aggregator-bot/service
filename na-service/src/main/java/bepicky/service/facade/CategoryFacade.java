package bepicky.service.facade;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.request.ListCategoryRequest;
import bepicky.common.domain.request.PickCategoryRequest;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.domain.response.ListCategoryResponse;
import bepicky.common.domain.response.PickCategoryResponse;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.domain.mapper.CategoryResponseMapper;
import bepicky.service.entity.Category;
import bepicky.service.entity.Reader;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryFacade {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IReaderService readerService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private CategoryResponseMapper categoryResponseMapper;

    public ListCategoryResponse listAll(ListCategoryRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:category:reader {} not found", request.getChatId());
            return new ListCategoryResponse(ErrorUtil.readerNotFound());
        }
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(reader, categoryService.findTopCategories(req));
    }

    public ListCategoryResponse listSub(ListCategoryRequest request) {
        Category parent = categoryService.find(request.getParentId()).orElse(null);
        if (parent == null) {
            log.warn("list:subcategory:parent category {} not found", request.getParentId());
            return new ListCategoryResponse(ErrorUtil.categoryNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:subcategory:reader {} not found", request.getChatId());
            return new ListCategoryResponse(ErrorUtil.readerNotFound());
        }
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(
            reader,
            categoryService.findByParent(parent, req)
        );
    }

    public PickCategoryResponse pick(PickCategoryRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("pick:category:reader {} not found", request.getChatId());
            return new PickCategoryResponse(ErrorUtil.readerNotFound());
        }
        Category category = categoryService.find(request.getCategoryId()).orElse(null);
        if (category == null) {
            log.warn("pick:category:category {} not found", request.getCategoryId());
            return new PickCategoryResponse(ErrorUtil.categoryNotFound());
        }
        reader.addSourcePages(sourcePageService.findByCategory(category));
        readerService.save(reader);
        return new PickCategoryResponse(
            reader.getPrimaryLanguage().getLang(),
            categoryResponseMapper.toFullResponse(category, reader.getPrimaryLanguage())
        );
    }

    private ListCategoryResponse getListCategoryResponse(Reader reader, Page<Category> categoryPage) {
        try {
            List<CategoryResponse> responses = categoryPage
                .stream()
                .map(c -> categoryResponseMapper.toFullResponse(c, reader.getPrimaryLanguage()))
                .collect(Collectors.toList());

            return new ListCategoryResponse(
                responses,
                categoryPage.isFirst(),
                categoryPage.isLast(),
                reader.getPrimaryLanguage().getLang()
            );
        } catch (ResourceNotFoundException e) {
            return new ListCategoryResponse(ErrorUtil.languageNotFound());
        }
    }


}
