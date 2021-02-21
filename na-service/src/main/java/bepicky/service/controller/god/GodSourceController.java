package bepicky.service.controller.god;

import bepicky.common.domain.dto.SourcePageDto;
import bepicky.service.entity.Source;
import bepicky.service.facade.functional.ISourceFunctionalFacade;
import bepicky.service.service.ISourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/god")
public class GodSourceController {

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourceFunctionalFacade sourceFunctionalFacade;

    @PutMapping("/source/{id}")
    public void enableSource(@PathVariable long id, @RequestParam String status) {
        Source.Status sourceStatus = Source.Status.valueOf(status.toUpperCase());
        sourceService.updateStatus(id, sourceStatus);
    }

    @PostMapping("/source/{id}/disable")
    public void disableSource(@PathVariable long id) {
        sourceService.disable(id);
    }

    @GetMapping("/source/list")
    public List<Source> listSources() {
        return sourceService.findAll();
    }

    @PutMapping("/source/{sourceId}/sourcepage/{spId}/change")
    public SourcePageDto changeSourcePageSource(@PathVariable long sourceId, @PathVariable long spId) {
        return sourceFunctionalFacade.changeSource(sourceId, spId);
    }
}