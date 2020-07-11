package vlad110kg.news.aggregator.data.ingestor.service;

@FunctionalInterface
public interface IngestionConsumer {

    void consume(String value, Object entity);
}
