package bepicky.service.controller.god;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bepicky.service.domain.NewsSyncResult;
import bepicky.service.service.INewsService;

@RestController
@RequestMapping("/god")
public class GodNewsController {

    @Autowired
    private INewsService newsService;

    @PutMapping("/news/sync/{name}")
    public NewsSyncResult sync(@PathVariable String name) {
        return newsService.sync(name);
    }
}
