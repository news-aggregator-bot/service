package bepicky.service.controller.god;

import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/god/ingest")
public class GodIngestionController {

    @Autowired
    private SourceIngestionService sourceService;

    @Autowired
    private LanguageIngestionService langService;

    @Autowired
    private CategoryIngestionService categoryService;

    @PostMapping(path = "/source", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void ingestSources(@RequestParam("file") MultipartFile file) throws IOException {
        sourceService.ingest(file.getInputStream());
    }

    @PostMapping(path = "/language", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void ingestLangs(@RequestParam("file") MultipartFile file) throws IOException {
        langService.ingest(file.getInputStream());
    }

    @PostMapping(path = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void ingestCategories(@RequestParam("file") MultipartFile file) throws IOException {
        categoryService.ingest(file.getInputStream());
    }
}
