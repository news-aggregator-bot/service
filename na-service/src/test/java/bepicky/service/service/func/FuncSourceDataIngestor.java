package bepicky.service.service.func;

import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.LocalisationIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import lombok.Builder;
import org.apache.commons.io.FilenameUtils;
import org.h2.store.fs.FilePathSplit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class FuncSourceDataIngestor {

    private static final String FUNC_SOURCE_DIR = "/func/parser/source/";

    private final SourceIngestionService sourceIS;
    private final CategoryIngestionService categoryIS;
    private final LocalisationIngestionService localisationIS;
    private final LanguageIngestionService languageIS;

    private boolean categoriesIngested;
    private boolean languagesIngested;

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

    public void ingestLocalisations() {
        if (!categoriesIngested) {
            ingestCategories();
        }
        localisationIS.ingest(openStream("localisations.xlsx"));
    }

    public List<String> listRealSourceFileNames() {
        try {
            return Files.list(Paths.get(getClass().getResource(FUNC_SOURCE_DIR + "real").getPath()))
                .map(Path::getFileName)
                .map(Path::toString)
                .map(FilenameUtils::getBaseName)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
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
