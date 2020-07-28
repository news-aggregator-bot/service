package vlad110kg.news.aggregator.web.reader;

import org.jsoup.nodes.Document;

public interface WebPageReader {

    Document read(String path);
}
