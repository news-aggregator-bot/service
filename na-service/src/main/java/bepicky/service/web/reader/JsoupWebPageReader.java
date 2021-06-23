package bepicky.service.web.reader;

import bepicky.service.exception.SourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupWebPageReader implements WebPageReader {

    @Override
    public Document read(String path) {
        try {
            return Jsoup.connect(path)
                .header("Content-Type", "*/*")
                .timeout(5000)
                .ignoreContentType(true)
                .get();
        } catch (IOException e) {
            throw new SourceException(e);
        }
    }
}
