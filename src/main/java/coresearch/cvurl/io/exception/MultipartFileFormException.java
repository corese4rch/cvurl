package coresearch.cvurl.io.exception;

/**
 * Thrown when a {@link java.io.IOException} exception occurs while reading part of a file.
 *
 * @since 1.0
 */
public class MultipartFileFormException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detailed message and
     * cause. <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's message.
     *
     * @param message - the detailed message is kept for later retrieval
     *                by the {@link #getMessage()} method.
     * @param cause - the cause is kept for later retrieval by the
     *                {@link #getCause()} method. A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.
     */
    public MultipartFileFormException(String message, Throwable cause) {
        super(message, cause);
    }
}
