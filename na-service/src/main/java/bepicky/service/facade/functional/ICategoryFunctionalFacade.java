package bepicky.service.facade.functional;

import bepicky.common.domain.request.CategoryRequest;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.service.domain.request.ListCategoryRequest;

public interface ICategoryFunctionalFacade {

    CategoryListResponse listAll(ListCategoryRequest request);

    CategoryListResponse listSub(ListCategoryRequest request);

    CategoryResponse pick(CategoryRequest request);

    CategoryResponse remove(CategoryRequest request);
}
