package bepicky.service.facade;

import bepicky.service.entity.Source;
import bepicky.service.entity.SourcePage;
import bepicky.service.exception.SourceNotFoundException;
import bepicky.service.service.ICategoryService;
import bepicky.service.service.IContentTagService;
import bepicky.service.service.ILanguageService;
import bepicky.service.service.ISourcePageService;
import bepicky.service.service.ISourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SourceFacade {

    @Autowired
    private ISourceService sourceService;

    @Autowired
    private ISourcePageService sourcePageService;

    @Autowired
    private IContentTagService contentTagService;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private ICategoryService categoryService;

    public List<Source> getAllSources() {
        return sourceService.findAll();
    }

    public List<SourcePage> getAllSourcePages(Long sourceId) {
        Source source = sourceService.find(sourceId).orElseThrow(SourceNotFoundException::new);
        return sourcePageService.findBySource(source);
    }
}
