package bepicky.service.web.reader;

import bepicky.service.exception.SourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
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

    @Override
    public String name() {
        return "JSOUP";
    }
}
