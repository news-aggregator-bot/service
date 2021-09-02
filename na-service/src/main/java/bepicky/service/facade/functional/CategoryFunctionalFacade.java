package bepicky.service.facade.functional;

import bepicky.common.ErrorUtil;
import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.dto.ReaderDto;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.exception.ResourceNotFoundException;
import bepicky.common.msg.CategoryCommandMsg;
import bepicky.common.msg.CategoryListMsg;
import bepicky.service.domain.mapper.CategoryDtoMapper;
import bepicky.service.entity.Category;
import bepicky.service.entity.CategoryType;
import bepicky.service.entity.Reader;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.IReaderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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
    private CategoryDtoMapper categoryResponseMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryListResponse listApplicable(CategoryListMsg m) {
        return readerService.findByChatId(m.getId()).map(r -> {
            CategoryType cType = CategoryType.valueOf(m.getType());
            Set<Category> applicableReadersCategories = getApplicable(r, cType);
            return new CategoryListResponse(
                applicableReadersCategories.stream().map(c -> toDto(r, c)).collect(Collectors.toList()),
                true,
                true,
                modelMapper.map(r, ReaderDto.class)
            );
        }).orElseGet(() -> {
            log.warn("list:category:reader {} not found", m.getId());
            return new CategoryListResponse(ErrorUtil.readerNotFound());
        });
    }

    @Override
    public CategoryListResponse listAll(CategoryListMsg m) {
        return readerService.findByChatId(m.getChatId())
            .map(reader -> {
                CategoryType type = CategoryType.valueOf(m.getType());
                return getListCategoryResponse(
                    reader,
                    categoryService.findTopCategories(type, pageReq(m.getPage(), m.getSize()))
                );
            }).orElseGet(() -> {
                log.warn("list:category:reader {} not found", m.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            });
    }

    @Override
    public CategoryListResponse sublist(CategoryListMsg m) {
        return categoryService.find(m.getId())
            .map(parent -> readerService.findByChatId(m.getChatId()).map(reader -> getListCategoryResponse(
                reader,
                categoryService.findByParent(parent, pageReq(m.getPage(), m.getSize()))
            )).orElseGet(() -> {
                log.warn("list:subcategory:reader {} not found", m.getChatId());
                return new CategoryListResponse(ErrorUtil.readerNotFound());
            })).orElseGet(() -> {
                log.warn("list:subcategory:parent category {} not found", m.getId());
                return new CategoryListResponse(ErrorUtil.categoryNotFound());
            });
    }

    @Override
    public CategoryResponse pickAll(CategoryCommandMsg m) {
        return doAction(m, Reader::addAllCategories);
    }

    @Override
    public CategoryResponse pick(CategoryCommandMsg m) {
        return doAction(m, Reader::addCategory);
    }

    @Override
    public CategoryResponse remove(CategoryCommandMsg m) {
        return doAction(m, Reader::removeCategory);
    }

    @Override
    public CategoryResponse removeAll(CategoryCommandMsg m) {
        return doAction(m, Reader::removeAllCategory);
    }

    @Override
    public CategoryDto deleteById(long id) {
        log.info("category:delete:{}", id);
        return categoryService.delete(id).map(c -> modelMapper.map(c, CategoryDto.class)).orElseGet(() -> {
            log.warn("category:delete:{}:404", id);
            return null;
        });
    }

    @Override
    public CategoryDto deleteByName(String name) {
        log.info("category:delete:{}", name);
        return categoryService.deleteByName(name).map(c -> modelMapper.map(c, CategoryDto.class)).orElseGet(() -> {
            log.warn("category:delete:{}:404", name);
            return null;
        });
    }

    private CategoryResponse doAction(CategoryCommandMsg m, BiConsumer<Reader, Category> action) {
        Reader reader = readerService.findByChatId(m.getChatId()).orElse(null);
        if (reader == null) {
            log.warn("category:{}:reader:{}:404", m.getCommand(), m.getChatId());
            return new CategoryResponse(ErrorUtil.readerNotFound());
        }
        Category category = categoryService.find(m.getCategoryId()).orElse(null);
        if (category == null) {
            log.warn("category:{}:{}:404", m.getCommand(), m.getCategoryId());
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

    private Set<Category> getApplicable(Reader r, CategoryType cType) {
        Set<Category> applicableReadersCategories = r.getSources().stream()
            .flatMap(s -> s.getPages().stream())
            .flatMap(sp -> sp.getCategories().stream())
            .filter(c -> c.getType().equals(cType))
            .collect(Collectors.toSet());
        return applicableReadersCategories.isEmpty() ? categoryService.getAllByType(cType) : applicableReadersCategories;
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
