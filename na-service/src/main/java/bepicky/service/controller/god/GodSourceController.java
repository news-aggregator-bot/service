package bepicky.service.controller.god;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.SourceEntity;
import bepicky.service.facade.functional.ISourceFunctionalFacade;
import bepicky.service.service.ISourcePageService;
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

    @Autowired
    private ISourcePageService sourcePageService;

    @PutMapping("/source/{id}")
    public void enableSource(@PathVariable long id, @RequestParam String status) {
        SourceEntity.Status sourceStatus = SourceEntity.Status.valueOf(status.toUpperCase());
        sourceService.updateStatus(id, sourceStatus);
    }

    @PostMapping("/source/{id}/disable")
    public void disableSource(@PathVariable long id) {
        sourceService.disable(id);
    }

    @GetMapping("/source/list")
    public List<SourceEntity> listSources() {
        return sourceService.findAll();
    }

    @GetMapping("/source/{id}")
    public SourceEntity getSource(@PathVariable long id) {
        return sourceService.find(id)
            .orElseThrow(() -> new ResourceNotFoundException("source not found"));
    }

    @PutMapping("/source/{sourceId}/sourcepage/{spId}/change")
    public void changeSourcePageSource(@PathVariable long sourceId, @PathVariable long spId) {
        sourceFunctionalFacade.changeSource(sourceId, spId);
    }

    @PutMapping("/source/{id}/fetch-period/{fetchPeriod}")
    public void disableSource(@PathVariable long id, @PathVariable String fetchPeriod) {
        SourceEntity.FetchPeriod sFetchPeriod = SourceEntity.FetchPeriod.valueOf(fetchPeriod);
        sourceService.updateFetchPeriod(id, sFetchPeriod);
    }

    @PutMapping("/source/page/{pageId}/enable")
    public void enablePage(@PathVariable("pageId") Long pageId) {
        sourcePageService.enable(pageId);
    }

    @PutMapping("/source/page/{pageId}/disable")
    public void disablePage(@PathVariable("pageId") Long pageId) {
        sourcePageService.disable(pageId);
    }
}