package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.model.Response;

/**
 * Thrown by {@link coresearch.cvurl.io.mapper.GenericMapper} when
 * any problems occur on response body deserialization
 */
public class ResponseMappingException extends MappingException {

    private final Response<String> response;

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param cause    the cause (which is saved for later retrieval by the
     *                 {@link #getCause()} method).  (A {@code null} value is
     *                 permitted, and indicates that the cause is nonexistent or
     *                 unknown.)
     * @param response response whose body parsing caused error
     */
    public ResponseMappingException(String message, Throwable cause, Response<String> response) {
        super(message, cause);
        this.response = response;
    }

    public Response<String> getResponse() {
        return response;
    }
}
