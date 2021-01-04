package bepicky.service.controller.god;

import bepicky.common.domain.dto.CategoryDto;
import bepicky.service.facade.functional.ICategoryFunctionalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/god/category")
public class GodCategoryController {

    @Autowired
    private ICategoryFunctionalFacade categoryFacade;

    @DeleteMapping("/delete/id/{id}")
    public CategoryDto deleteById(@PathVariable long id) {
        return categoryFacade.deleteById(id);
    }

    @DeleteMapping("/delete/name/{name}")
    public CategoryDto deleteById(@PathVariable String name) {
        return categoryFacade.deleteByName(name);
    }

}
