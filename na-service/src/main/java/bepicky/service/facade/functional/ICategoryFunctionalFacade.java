package bepicky.service.facade.functional;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.common.msg.CategoryCommandMsg;
import bepicky.common.msg.CategoryListMsg;

public interface ICategoryFunctionalFacade {

    CategoryListResponse listApplicable(CategoryListMsg m);

    CategoryListResponse listAll(CategoryListMsg m);

    CategoryListResponse sublist(CategoryListMsg m);

    CategoryResponse pickAll(CategoryCommandMsg m);

    CategoryResponse pick(CategoryCommandMsg m);

    CategoryResponse remove(CategoryCommandMsg m);

    CategoryResponse removeAll(CategoryCommandMsg m);

    CategoryDto deleteById(long id);

    CategoryDto deleteByName(String name);
}
