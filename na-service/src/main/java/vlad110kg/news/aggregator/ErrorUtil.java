package vlad110kg.news.aggregator;

import vlad110kg.news.aggregator.domain.response.ErrorResponse;

public class ErrorUtil {

    private ErrorUtil() {}

    public static ErrorResponse languageNotFound() {
        return ErrorResponse.builder().code(404).entity("language").build();
    }

    public static ErrorResponse categoryNotFound() {
        return ErrorResponse.builder().code(404).entity("category").build();
    }

    public static ErrorResponse sourcePageNotFound() {
        return ErrorResponse.builder().code(404).entity("source page").build();
    }

    public static ErrorResponse readerNotFound() {
        return ErrorResponse.builder().code(404).entity("reader").build();
    }
}
