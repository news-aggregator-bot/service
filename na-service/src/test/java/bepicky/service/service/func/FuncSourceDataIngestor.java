package bepicky.service.service.func;

import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import lombok.Builder;

import java.io.IOException;
import java.io.InputStream;

@Builder
public class FuncSourceDataIngestor {

    private static final String FUNC_SOURCE_DIR = "/func/parser/source/";

    private final SourceIngestionService sourceIS;
    private final CategoryIngestionService categoryIS;
    private final LanguageIngestionService languageIS;

    private boolean categoriesIngested;
    private boolean languagesIngested;

    public void ingestSources() {
        ingestSources("Sources");
    }

    public void ingestSources(String sourceName) {
        if (!categoriesIngested) {
            ingestCategories();
        }
        sourceIS.ingest(openStream(sourceName + ".xlsx"));
    }

    public void ingestCategories() {
        if (!categoriesIngested) {
            ingestLanguages();
            categoryIS.ingest(openStream("Categories.xlsx"));
            categoriesIngested = true;
        }
    }

    public void ingestLanguages() {
        if (!languagesIngested) {
            languageIS.ingest(openStream("Languages.xlsx"));
            languagesIngested = true;
        }
    }

    private InputStream openStream(String s) {
        try {
            return getClass().getResource(FUNC_SOURCE_DIR + s).openStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
