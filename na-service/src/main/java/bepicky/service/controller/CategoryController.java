package bepicky.service.controller;

import bepicky.common.domain.response.ListCategoryResponse;
import bepicky.common.domain.response.PickCategoryResponse;
import bepicky.service.domain.request.ListCategoryRequest;
import bepicky.service.domain.request.PickCategoryRequest;
import bepicky.service.facade.CategoryFacade;
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
    private CategoryFacade categoryFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/category/list")
    public ListCategoryResponse findAll(@RequestParam Map<String, Object> params) {
        ListCategoryRequest request = objectMapper.convertValue(params, ListCategoryRequest.class);
        return request.getParentId() == 0 ? categoryFacade.listAll(request) : categoryFacade.listSub(request);
    }

    @PostMapping("/category/pick")
    public PickCategoryResponse pick(@RequestBody PickCategoryRequest request) {
        return categoryFacade.pick(request);
    }
}
