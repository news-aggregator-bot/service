package bepicky.service.controller;

import bepicky.common.domain.request.CategoryRequest;
import bepicky.common.domain.response.CategoryListResponse;
import bepicky.common.domain.response.CategoryResponse;
import bepicky.service.domain.request.ListCategoryRequest;
import bepicky.service.facade.functional.ICategoryFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private ICategoryFunctionalFacade categoryFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/category/list")
    public CategoryListResponse findAll(@RequestParam Map<String, Object> params) {
        ListCategoryRequest request = objectMapper.convertValue(params, ListCategoryRequest.class);
        return request.getParentId() == 0 ? categoryFacade.listAll(request) : categoryFacade.sublist(request);
    }

    @GetMapping("/category/list/picked")
    public CategoryListResponse findPicked(@RequestParam Map<String, Object> params) {
        ListCategoryRequest request = objectMapper.convertValue(params, ListCategoryRequest.class);
        return request.getParentId() == 0 ? categoryFacade.listPicked(request) : categoryFacade.sublistPicked(request);
    }

    @GetMapping("/category/list/notpicked")
    public CategoryListResponse findNotPicked(@RequestParam Map<String, Object> params) {
        ListCategoryRequest request = objectMapper.convertValue(params, ListCategoryRequest.class);
        return request.getParentId() == 0 ?
            categoryFacade.listNotPicked(request) :
            categoryFacade.sublistNotPicked(request);
    }

    @PostMapping("/category/pick/all")
    public CategoryResponse pickAll(@RequestBody CategoryRequest request) {
        return categoryFacade.pickAll(request);
    }

    @PostMapping("/category/pick")
    public CategoryResponse pick(@RequestBody CategoryRequest request) {
        return categoryFacade.pick(request);
    }

    @PostMapping("/category/remove")
    public CategoryResponse remove(@RequestBody CategoryRequest request) {
        return categoryFacade.remove(request);
    }
}
