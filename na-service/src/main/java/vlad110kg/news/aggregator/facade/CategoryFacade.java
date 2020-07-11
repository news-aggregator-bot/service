package vlad110kg.news.aggregator.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import vlad110kg.news.aggregator.ErrorUtil;
import vlad110kg.news.aggregator.domain.mapper.CategoryResponseMapper;
import vlad110kg.news.aggregator.domain.request.ListCategoryRequest;
import vlad110kg.news.aggregator.domain.request.PickCategoryRequest;
import vlad110kg.news.aggregator.domain.response.CategoryResponse;
import vlad110kg.news.aggregator.domain.response.ListCategoryResponse;
import vlad110kg.news.aggregator.domain.response.PickCategoryResponse;
import vlad110kg.news.aggregator.entity.Category;
import vlad110kg.news.aggregator.entity.Reader;
import vlad110kg.news.aggregator.exception.ResourceNotFoundException;
import vlad110kg.news.aggregator.service.ICategoryService;
import vlad110kg.news.aggregator.service.ILanguageService;
import vlad110kg.news.aggregator.service.IReaderService;
import vlad110kg.news.aggregator.service.ISourcePageService;

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
            return ListCategoryResponse.error(ErrorUtil.readerNotFound());
        }
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(
            reader,
            categoryService.findTopCategories(req),
            categoryService.countTopCategories()
        );
    }

    public ListCategoryResponse listSub(ListCategoryRequest request) {
        Category parent = categoryService.find(request.getParentId()).orElse(null);
        if (parent == null) {
            log.warn("list:subcategory:parent category {} not found", request.getParentId());
            return ListCategoryResponse.error(ErrorUtil.categoryNotFound());
        }
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("list:subcategory:reader {} not found", request.getChatId());
            return ListCategoryResponse.error(ErrorUtil.readerNotFound());
        }
        PageRequest req = PageRequest.of(request.getPage() - 1, request.getSize());
        return getListCategoryResponse(
            reader,
            categoryService.findByParent(parent, req),
            categoryService.countByParent(parent)
        );
    }

    public PickCategoryResponse pick(PickCategoryRequest request) {
        Reader reader = readerService.find(request.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("pick:category:reader {} not found", request.getChatId());
            return PickCategoryResponse.error(ErrorUtil.readerNotFound());
        }
        Category category = categoryService.find(request.getCategoryId()).orElse(null);
        if (category == null) {
            log.warn("pick:category:category {} not found", request.getCategoryId());
            return PickCategoryResponse.error(ErrorUtil.categoryNotFound());
        }
        reader.addSourcePages(sourcePageService.findByCategory(category));
        readerService.save(reader);
        return PickCategoryResponse.builder()
            .category(categoryResponseMapper.toFullResponse(category, reader.getPrimaryLanguage()))
            .language(reader.getPrimaryLanguage().getLang())
            .build();
    }

    private ListCategoryResponse getListCategoryResponse(Reader reader, List<Category> categories, long totalAmount) {
        try {
            List<CategoryResponse> responses = categories
                .stream()
                .map(c -> categoryResponseMapper.toFullResponse(c, reader.getPrimaryLanguage()))
                .collect(Collectors.toList());

            return ListCategoryResponse.builder()
                .categories(responses)
                .language(reader.getPrimaryLanguage().getLang())
                .totalAmount(totalAmount)
                .build();
        } catch (ResourceNotFoundException e) {
            return ListCategoryResponse.error(ErrorUtil.languageNotFound());
        }
    }


}
