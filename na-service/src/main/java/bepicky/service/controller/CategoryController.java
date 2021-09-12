package bepicky.service.controller;

import bepicky.common.domain.response.CategoryListResponse;
import bepicky.service.domain.request.ListCategoryRequest;
import bepicky.service.facade.functional.ICategoryFunctionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private ICategoryFunctionalFacade categoryFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/category/{type}/reader/{chatId}/list")
    public CategoryListResponse findAll(
        @PathVariable("type") String type,
        @PathVariable("chatId") Long chatId
    ) {
        return categoryFacade.listApplicable(chatId, type);
    }

    @GetMapping("/category/list")
    public CategoryListResponse findAll(@RequestParam Map<String, Object> params) {
        ListCategoryRequest request = objectMapper.convertValue(params, ListCategoryRequest.class);
        return request.getParentId() == 0 ?
            categoryFacade.listAll(request) :
            categoryFacade.sublist(request);
    }
}

