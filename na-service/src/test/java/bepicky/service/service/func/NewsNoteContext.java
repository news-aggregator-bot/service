package bepicky.service.service.func;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;
import bepicky.service.entity.NewsNote;
import bepicky.service.service.func.util.FuncUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static bepicky.service.service.func.util.FuncUtil.normalizeName;

public class NewsNoteContext {

    private static final String NOTES_DIR = "/func/parser/notes";

    private Map<String, Map<String, Path>> newsNotes;

    private final TypeReference<Set<NewsNote>> type = new TypeReference<Set<NewsNote>>() {};

    private final ObjectMapper objectMapper;

    public NewsNoteContext(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        initContext();
    }

    private void initContext() {
        ImmutableMap.Builder<String, Map<String, Path>> s = ImmutableMap.builder();
        File notesDirFilePath = Paths.get(getClass().getResource(NOTES_DIR).getPath()).toFile();
        for (File sourceDir : notesDirFilePath.listFiles()) {
            ImmutableMap.Builder<String, Path> p = ImmutableMap.builder();
            File[] pages = sourceDir.listFiles();
            for (File page : pages) {
                p.put(FilenameUtils.getBaseName(page.getName()), page.toPath());
            }
            s.put(sourceDir.getName(), p.build());
        }
        newsNotes = s.build();
    }

    public Set<NewsNote> get(String sourceName, String sourcePageName) {
        try {
            sourceName = FuncUtil.normalizeName(sourceName);
            if (!newsNotes.containsKey(sourceName)) {
                throw new IllegalArgumentException("source:notfound:" + sourceName);
            }
            sourcePageName = FuncUtil.normalizeName(sourcePageName);
            Path path = newsNotes.get(sourceName).get(sourcePageName);
            if (path == null) {
                throw new IllegalArgumentException("sourcepage:notfound:" + sourcePageName);
            }
            return objectMapper.readValue(path.toFile(), type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void approve(String sourceName, String sourcePageName, Set<NewsNote> notes) {
        try {
            sourceName = FuncUtil.normalizeName(sourceName);
            sourcePageName = FuncUtil.normalizeName(sourcePageName);

            Path resolveSourcePagePath = resolveSourcePagePath(sourceName, sourcePageName);

            objectMapper.writeValue(new FileOutputStream(resolveSourcePagePath.toFile()), notes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Path resolveSourcePagePath(String sourceName, String sourcePageName) {
        try {
            sourceName = normalizeName(sourceName);
            sourcePageName = normalizeName(sourcePageName);
            Path sourcePageDir = Files.createDirectories(
                Paths.get(getClass().getResource(NOTES_DIR).getPath()).resolve(sourceName.toLowerCase())
            );
            return sourcePageDir.resolve(sourcePageName + ".json");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}
