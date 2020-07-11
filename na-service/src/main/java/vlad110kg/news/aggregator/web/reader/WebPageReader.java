package vlad110kg.news.aggregator.web.reader;

public interface WebPageReader<T> {

    T read(String path);
}
