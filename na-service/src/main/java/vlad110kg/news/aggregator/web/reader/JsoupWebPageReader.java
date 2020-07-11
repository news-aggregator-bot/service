package vlad110kg.news.aggregator.web.reader;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import vlad110kg.news.aggregator.exception.SourceException;

import org.springframework.stereotype.Service;

@Service("jsoupReader")
public class JsoupWebPageReader implements WebPageReader<Document> {

    @Override
    public Document read(String path) {
        try {
            return Jsoup.connect(path)
                .header("Content-Type", "*/*")
                .ignoreContentType(true)
                .get();
        } catch (IOException e) {
            throw new SourceException(e);
        }
    }
}
