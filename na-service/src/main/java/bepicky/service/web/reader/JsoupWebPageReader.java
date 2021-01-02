package bepicky.service.web.reader;

import bepicky.service.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
public class JsoupWebPageReader implements WebPageReader {

    @Override
    public Document read(String path) {
        try {
            log.info("webpagereader:jsoup:read:{}", path);
            return Jsoup.connect(path)
                .header("Content-Type", "*/*")
                .ignoreContentType(true)
                .get();
        } catch (IOException e) {
            throw new SourceException(e);
        }
    }
}
