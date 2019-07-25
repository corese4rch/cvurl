package coresearch.cvurl.io.exception;

public class ResponseBodyHandlingException extends RuntimeException {

    public ResponseBodyHandlingException(String message) {
        super(message);
    }

    public ResponseBodyHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
