package vlad110kg.news.aggregator.exception;

public class SourceNotFoundException extends SourceException {

    public SourceNotFoundException() {
    }

    public SourceNotFoundException(String message) {
        super(message);
    }

    public SourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceNotFoundException(Throwable cause) {
        super(cause);
    }

    public SourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
