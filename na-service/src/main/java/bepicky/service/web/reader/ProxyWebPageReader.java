package bepicky.service.web.reader;

import bepicky.service.exception.SourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ProxyWebPageReader implements WebPageReader {

    @Override
    public Document read(String path) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8118));
            return Jsoup.connect(path)
                .proxy(proxy)
                .header("Content-Type", "*/*")
                .timeout(15000)
                .ignoreContentType(true)
                .get();
        } catch (IOException e) {
            throw new SourceException(e);
        }
    }
}
