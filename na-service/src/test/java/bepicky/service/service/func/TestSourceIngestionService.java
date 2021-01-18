package bepicky.service.service.func;

import bepicky.service.data.ingestor.service.SourceIngestionService;

import java.net.MalformedURLException;
import java.net.URL;

public class TestSourceIngestionService extends SourceIngestionService {

    @Override
    protected String normaliseUrl(String url) {
        try {
            URL u = new URL(url);
            return url.replace(u.getHost(), "localhost:8080");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
