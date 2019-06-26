package coreserech.cvurl.io.exception;

public class MappingException extends RuntimeException {

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable t) {
        super(message, t);
    }
}
