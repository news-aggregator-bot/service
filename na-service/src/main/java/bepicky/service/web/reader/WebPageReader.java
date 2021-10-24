package bepicky.service.web.reader;

import org.jsoup.nodes.Document;

public interface WebPageReader {

    Document read(String path);

    String name();
}
