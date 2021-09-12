package bepicky.service.facade.functional;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.msg.CategoryCommandMsg;
import bepicky.common.msg.CategoryListMsg;
import bepicky.service.domain.request.ListCategoryRequest;

public interface ICategoryFunctionalFacade {

    CategoryListResponse listApplicable(Long chatId, String type);

    CategoryListResponse listApplicable(CategoryListMsg m);

    CategoryListResponse listAll(ListCategoryRequest request);

    CategoryListResponse listAll(CategoryListMsg m);

    CategoryListResponse sublist(ListCategoryRequest request);

    CategoryListResponse sublist(CategoryListMsg m);

    CategoryResponse pickAll(CategoryCommandMsg m);

    CategoryResponse pick(CategoryCommandMsg m);

    CategoryResponse remove(CategoryCommandMsg m);

    CategoryResponse removeAll(CategoryCommandMsg m);

    CategoryDto deleteById(long id);

    CategoryDto deleteByName(String name);
}
