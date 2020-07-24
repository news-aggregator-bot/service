package vlad110kg.news.aggregator.service.func;

import lombok.Builder;
import vlad110kg.news.aggregator.data.ingestor.service.CategoryIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.LanguageIngestionService;
import vlad110kg.news.aggregator.data.ingestor.service.SourceIngestionService;

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
        if (!categoriesIngested) {
            ingestCategories();
        }
        sourceIS.ingest(openStream("Sources.xlsx"));
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
