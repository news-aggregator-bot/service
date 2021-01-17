package bepicky.service.data.ingestor.exception;

public class DataIngestionException extends RuntimeException{

    public DataIngestionException(String message) {
        super(message);
    }

    public DataIngestionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIngestionException(Throwable cause) {
        super(cause);
    }
}
