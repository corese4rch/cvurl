package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.model.Response;

/**
 * Thrown by {@link coresearch.cvurl.io.mapper.GenericMapper} when it tries to deserialize an invalid response body.
 *
 * @since 1.0
 */
public class ResponseMappingException extends MappingException {

    private final Response<String> response;

    /**
     * Constructs a new exception with the specified detailed message and
     * cause. <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's message.
     *
     * @param message - the detailed message is kept for later retrieval
     *                 by the {@link #getMessage()} method.
     * @param cause - the cause is kept for later retrieval by the
     *                 {@link #getCause()} method. A {@code null} value is
     *                 permitted, and indicates that the cause is nonexistent or
     *                 unknown.
     * @param response - the response whose body caused a parsing error
     */
    public ResponseMappingException(String message, Throwable cause, Response<String> response) {
        super(message, cause);
        this.response = response;
    }

    public Response<String> getResponse() {
        return response;
    }
}
