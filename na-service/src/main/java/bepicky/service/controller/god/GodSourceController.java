package bepicky.service.controller.god;

import bepicky.common.exception.ResourceNotFoundException;
import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.facade.functional.ISourceFunctionalFacade;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class GodSourceController {

    private final ISourceService sourceService;
    private final ISourceFunctionalFacade sourceFunctionalFacade;
    private final ISourcePageService sourcePageService;

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

    @GetMapping("/source/{id}")
    public Source getSource(@PathVariable long id) {
        return sourceService.find(id)
            .orElseThrow(() -> new ResourceNotFoundException("source not found"));
    }

    @PutMapping("/source/{sourceId}/sourcepage/{spId}/change")
    public void changeSourcePageSource(@PathVariable long sourceId, @PathVariable long spId) {
        sourceFunctionalFacade.changeSource(sourceId, spId);
    }

    @PutMapping("/source/{id}/fetch-period/{fetchPeriod}")
    public void disableSource(@PathVariable long id, @PathVariable String fetchPeriod) {
        Source.FetchPeriod sFetchPeriod = Source.FetchPeriod.valueOf(fetchPeriod);
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

    @GetMapping("/source/{id}/pages/{pageId}")
    public SourcePage getSourcePage(@PathVariable("id") Long id, @PathVariable("pageId") Long pageId) {
        Source source = sourceService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("source not found"));
        SourcePage sourcePage = sourcePageService.findById(pageId)
            .orElseThrow(() -> new IllegalArgumentException("source page not found"));
        if (source.getPages().contains(sourcePage)) {
            return sourcePage;
        }
        throw new IllegalArgumentException("source page doesn't belong to the source");
    }

    @GetMapping("/source/{id}/pages")
    public List<SourcePage> listSourcePages(@PathVariable("id") Long id) {
        return sourceService.findById(id)
            .map(sourcePageService::findBySource)
            .orElseThrow(() -> new IllegalArgumentException("source not found"));
    }
}