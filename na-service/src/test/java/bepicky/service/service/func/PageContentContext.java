package bepicky.service.service.func;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static bepicky.service.service.func.util.FuncUtil.normalizeName;

public class PageContentContext {

    private static final String PAGE_DIR = "/func/parser/page";
    private final Map<String, Map<String, Path>> pages;

    public PageContentContext() {
        ImmutableMap.Builder<String, Map<String, Path>> s = ImmutableMap.builder();
        File pageDirFilePath = Paths.get(getClass().getResource(PAGE_DIR).getPath()).toFile();
        for (File sourceDir : pageDirFilePath.listFiles()) {
            ImmutableMap.Builder<String, Path> p = ImmutableMap.builder();
            File[] pages = sourceDir.listFiles();
            if (pages != null) {
                for (File page : pages) {
                    p.put(FilenameUtils.getBaseName(page.getName()), page.toPath());
                }
            }
            s.put(sourceDir.getName(), p.build());
        }
        pages = s.build();
    }

    public byte[] get(String sourceName, String sourcePageName) {
        try {
            sourceName = normalizeName(sourceName);
            if (!pages.containsKey(sourceName)) {
                throw new IllegalArgumentException("source:notfound:" + sourceName);
            }
            sourcePageName = normalizeName(sourcePageName);
            Path path = pages.get(sourceName).get(sourcePageName);
            if (path == null) {
                return new byte[0];
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean exists(String sourceName, String sourcePageName) {
        Path resolvedPageContentFile = resolveSourcePagePath(sourceName, sourcePageName);
        return Files.exists(resolvedPageContentFile);
    }

    public Path approve(String sourceName, String sourcePageName, String pageContent) {

        Path resolvedPageContentFile = resolveSourcePagePath(sourceName, sourcePageName);
        if (Files.exists(resolvedPageContentFile)) {
            return resolvedPageContentFile;
        }
        try {
            Path pageContentFile = Files.createFile(resolvedPageContentFile);
            return Files.write(pageContentFile, pageContent.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Path resolveSourcePagePath(String sourceName, String sourcePageName) {
        try {
            sourceName = normalizeName(sourceName);
            sourcePageName = normalizeName(sourcePageName);
            Path sourcePageDir = Files.createDirectories(
                Paths.get(getClass().getResource(PAGE_DIR).getPath()).resolve(sourceName.toLowerCase())
            );
            return sourcePageDir.resolve(sourcePageName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

}
