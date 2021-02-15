package bepicky.service.controller;

import bepicky.common.domain.dto.NewsNoteDto;
import bepicky.common.domain.request.NewsSearchRequest;
import bepicky.common.domain.response.NewsSearchResponse;
import bepicky.service.dto.Ids;
import bepicky.service.facade.functional.INewsNoteFunctionalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private INewsNoteFunctionalFacade newsFacade;

    @PostMapping("/search")
    public NewsSearchResponse search(@RequestBody NewsSearchRequest request) {
        return newsFacade.search(request);
    }

    @PutMapping("/refresh")
    public List<NewsNoteDto> refresh(@RequestBody Ids ids) {
        return newsFacade.refresh(ids);
    }

}
