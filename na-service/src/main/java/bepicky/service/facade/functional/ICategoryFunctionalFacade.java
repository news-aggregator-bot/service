package bepicky.service.facade.functional;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.request.CategoryRequest;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.service.domain.request.ListCategoryRequest;

public interface ICategoryFunctionalFacade {

    CategoryListResponse listApplicable(Long chatId, String type);

    CategoryListResponse listAll(ListCategoryRequest request);

    CategoryListResponse sublist(ListCategoryRequest request);

    CategoryResponse pickAll(CategoryRequest request);

    CategoryResponse pick(CategoryRequest request);

    CategoryResponse remove(CategoryRequest request);

    CategoryResponse removeAll(CategoryRequest request);

    CategoryDto deleteById(long id);

    CategoryDto deleteByName(String name);
}
