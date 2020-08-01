package bepicky.service.data.ingestor.service;

import java.io.InputStream;

public interface IngestionService {

    void ingest(InputStream data);
}
