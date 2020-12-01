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
import bepicky.service.service.ICategoryService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.IReaderService;
import bepicky.service.service.ISourcePageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryFunctionalFacade implements ICategoryFunctionalFacade, CommonFunctionalFacade {

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
        return readerService.find(request.getChatId())
            .map(reader -> {
                CategoryType type = CategoryType.valueOf(request.getType());
                return getListCategoryResponse(
                    reader,
                    categoryService.findTopCategories(type, pageReq(request.getPage(), request.getSize()))
                );
            }).orElseGet(() -> {
                log.warn("list:category:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryListResponse sublist(ListCategoryRequest request) {
        return categoryService.find(request.getParentId())
            .map(parent -> readerService.find(request.getChatId()).map(reader -> getListCategoryResponse(
                reader,
                categoryService.findByParent(parent, pageReq(request.getPage(), request.getSize()))
            )).orElseGet(() -> {
                log.warn("list:subcategory:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            })).orElseGet(() -> {
                log.warn("list:subcategory:parent category {} not found", request.getParentId());
                return new CategoryListResponse(ErrorUtil.categoryNotFound());
            });
    }

    @Override
    public CategoryListResponse listPicked(ListCategoryRequest request) {
        return readerService.find(request.getChatId())
            .map(reader -> {
                log.debug("list:picked category:reader:{}:type:{}", reader.getId(), request.getType());
                CategoryType type = CategoryType.valueOf(request.getType());

                return getListCategoryResponse(
                    reader,
                    categoryService.findPickedTopCategories(
                        reader,
                        type,
                        pageReq(request.getPage(), request.getSize())
                    )
                );
            })
            .orElseGet(() -> {
                log.warn("list:picked category:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryListResponse sublistPicked(ListCategoryRequest request) {
        return readerService.find(request.getChatId())
            .map(reader -> categoryService.find(request.getParentId()).map(
                parent -> getListCategoryResponse(
                    reader,
                    categoryService.findPickedCategoriesByParent(
                        reader,
                        parent,
                        pageReq(request.getPage(), request.getSize())
                    )
                )
            ).orElseGet(() -> {
                log.warn("list:subcategory:parent category {} not found", request.getParentId());
                return new CategoryListResponse(ErrorUtil.categoryNotFound());
            })).orElseGet(() -> {
                log.warn("list:subcategory:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryListResponse listNotPicked(ListCategoryRequest request) {
        return readerService.find(request.getChatId())
            .map(reader -> {
                log.debug("list:not picked category:reader:{}:type:{}", reader.getId(), request.getType());
                CategoryType type = CategoryType.valueOf(request.getType());

                return getListCategoryResponse(
                    reader,
                    categoryService.findNotPickedTopCategories(
                        reader,
                        type,
                        pageReq(request.getPage(), request.getSize())
                    )
                );
            })
            .orElseGet(() -> {
                log.warn("list:not picked category:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryListResponse sublistNotPicked(ListCategoryRequest request) {
        return readerService.find(request.getChatId())
            .map(reader -> categoryService.find(request.getParentId()).map(
                parent -> getListCategoryResponse(
                    reader,
                    categoryService.findNotPickedCategoriesByParent(
                        reader,
                        parent,
                        pageReq(request.getPage(), request.getSize())
                    )
                )
            ).orElseGet(() -> {
                log.warn("list:subcategory:parent category {} not found", request.getParentId());
                return new CategoryListResponse(ErrorUtil.categoryNotFound());
            })).orElseGet(() -> {
                log.warn("list:subcategory:reader {} not found", request.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryResponse pickAll(CategoryRequest request) {
        return doAction(request, Reader::addAllCategories);
    }

    @Override
    public CategoryResponse pick(CategoryRequest request) {
        return doAction(request, Reader::addCategory);
    }

    @Override
    public CategoryResponse remove(CategoryRequest request) {
        return doAction(request, Reader::removeCategory);
    }

    @Override
    public CategoryResponse removeAll(CategoryRequest request) {
        return doAction(request, Reader::removeAllCategory);
    }

    @Override
    public CategoryDto deleteById(long id) {
        return categoryService.delete(id).map(c -> modelMapper.map(c, CategoryDto.class)).orElse(null);
    }

    @Override
    public CategoryDto deleteByName(String name) {
        return categoryService.deleteByName(name).map(c -> modelMapper.map(c, CategoryDto.class)).orElse(null);
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
                .map(c -> toDto(reader, c))
                .collect(Collectors.toList());

            return new CategoryListResponse(
                dtos,
                categoryPage.isFirst(),
                categoryPage.isLast(),
                modelMapper.map(reader, ReaderDto.class)
            );
        } catch (ResourceNotFoundException e) {
            return new CategoryListResponse(ErrorUtil.categoryNotFound());
        }
    }

    private CategoryDto toDto(Reader reader, Category c) {
        CategoryDto dto = categoryResponseMapper.toFullDto(c, reader.getPrimaryLanguage());
        dto.setPicked(reader.getCategories().contains(c));
        if (dto.getParent() != null) {
            dto.getParent().setPicked(reader.getCategories().contains(c.getParent()));
        }
        return dto;
    }

}
