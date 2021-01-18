package bepicky.service.service.func;

import bepicky.service.data.ingestor.service.CategoryIngestionService;
import bepicky.service.data.ingestor.service.LanguageIngestionService;
import bepicky.service.data.ingestor.service.SourceIngestionService;
import lombok.Builder;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class FuncSourceDataIngestor {

    private static final String FUNC_SOURCE_DIR = "/func/parser/source/";

    private final SourceIngestionService sourceIS;

    public void ingestSources() {
        listRealSourceFileNames().forEach(this::ingestSources);
    }

    public void ingestSources(String sourceName) {
        sourceIS.ingest(openStream("real/" + sourceName + ".xlsx"));
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
