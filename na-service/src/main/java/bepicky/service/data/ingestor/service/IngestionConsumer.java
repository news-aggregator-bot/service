package bepicky.service.data.ingestor.service;

@FunctionalInterface
public interface IngestionConsumer {

    void consume(String value, Object entity);
}
