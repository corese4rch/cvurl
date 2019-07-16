package coresearch.cvurl.io.exception;

/**
 * Thrown by {@link coresearch.cvurl.io.util.Url} when try to create malformed url.
 */
public class BadUrlException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public BadUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
