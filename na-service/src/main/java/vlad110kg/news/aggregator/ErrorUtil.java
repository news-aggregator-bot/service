package vlad110kg.news.aggregator;

import vlad110kg.news.aggregator.domain.response.ErrorResponse;

public class ErrorUtil {

    public static final String LANGUAGE = "language";
    public static final String CATEGORY = "category";
    public static final String SOURCE_PAGE = "source page";
    public static final String READER = "reader";

    private ErrorUtil() {}

    public static ErrorResponse languageNotFound() {
        return ErrorResponse.builder().code(404).entity(LANGUAGE).build();
    }

    public static ErrorResponse categoryNotFound() {
        return ErrorResponse.builder().code(404).entity(CATEGORY).build();
    }

    public static ErrorResponse sourcePageNotFound() {
        return ErrorResponse.builder().code(404).entity(SOURCE_PAGE).build();
    }

    public static ErrorResponse readerNotFound() {
        return ErrorResponse.builder().code(404).entity(READER).build();
    }

    public static ErrorResponse parseError(String message) {
        String[] errors = message.split(":");
        String entity = errors[0];
        int code = Integer.parseInt(errors[1]);
        return ErrorResponse.builder().code(code).entity(entity).build();
    }
}
