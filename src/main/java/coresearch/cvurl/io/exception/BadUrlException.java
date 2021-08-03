package coresearch.cvurl.io.exception;

/**
 * Thrown by {@link coresearch.cvurl.io.util.Url} when it tries to generate an invalid URL.
 *
 * @since 0.9
 */
public class BadUrlException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detailed message and
     * cause. <p>Note that the detailed message associated with
     * the {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's message.
     *
     * @param message - the detailed message is kept for later retrieval
     *                by the {@link #getMessage()} method.
     * @param cause - the cause is kept for later retrieval by the
     *                {@link #getCause()} method. A {@code null} value is
     *                permitted and indicates that the cause is nonexistent or
     *                unknown.
     */
    public BadUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
