package vlad110kg.news.aggregator.controller.god;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vlad110kg.news.aggregator.entity.Source;
import vlad110kg.news.aggregator.entity.SourcePage;
import vlad110kg.news.aggregator.facade.SourceFacade;

import java.util.List;

@RestController
@RequestMapping("/god")
public class GodSourceController {

    @Autowired
    private SourceFacade sourceFacade;

//    @PostMapping("/source")
//    public Source create(@Valid @RequestBody SourceRequest request) {
//        return sourceFacade.createSource(request);
//    }

    @GetMapping("/sources")
    public List<Source> allSources() {
        return sourceFacade.getAllSources();
    }

    @GetMapping("/source/{sourceId}/pages")
    public List<SourcePage> allSourcePages(@PathVariable Long sourceId) {
        return sourceFacade.getAllSourcePages(sourceId);
    }

//    @GetMapping("/source/{sourceId}/content")
//    public List<ContentTag> allSourceContentTags(@PathVariable Long sourceId) {
//        return sourceFacade.getAllSourceContentTags(sourceId);
//    }

//    @PostMapping("/source/{name}/page")
//    public SourcePage createPage(@PathVariable String name, @Valid @RequestBody SourcePageRequest page) {
//        return sourceFacade.createSourcePage(name, page);
//    }
//
//    @PostMapping("/source/{name}/pages")
//    public Collection<SourcePage> createPages(
//        @PathVariable String name,
//        @Valid @RequestBody List<SourcePageRequest> pages
//    ) {
//        return sourceFacade.createSourcePages(name, pages);
//    }
//
//    @PostMapping("/source/{name}/content")
//    public ContentTag createContentTag(
//        @PathVariable String name,
//        @Valid @RequestBody ContentTagRequest tag
//    ) {
//        return sourceFacade.createContentTag(name, tag);
//    }
//
//    @PostMapping("/source/{name}/contents")
//    public Collection<ContentTag> createContentTags(
//        @PathVariable String name,
//        @Valid @RequestBody List<ContentTagRequest> tags
//    ) {
//        return sourceFacade.createContentTags(name, tags);
//    }

}
