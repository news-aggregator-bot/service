package bepicky.service.perf;

import bepicky.service.domain.RawNews;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Getter
public class RawNewsContext {
    private final List<RawNews> rawNews;

    public RawNewsContext() {
        ImmutableList.Builder<RawNews> rawNews = ImmutableList.builder();
        ObjectMapper om = new ObjectMapper();
        URL funcDir = RawNewsContext.class.getClassLoader().getResource("raw_news");
        if (funcDir != null) {
            File pageDirFilePath = Paths.get(funcDir.getPath()).toFile();
            File[] pages = pageDirFilePath.listFiles();
            for (File page : pages) {
                try {
                    rawNews.add(om.readValue(Files.readAllBytes(page.toPath()), RawNews.class));
                } catch (IOException ignore) {
                }
            }
        }
        this.rawNews = rawNews.build();
    }
}
