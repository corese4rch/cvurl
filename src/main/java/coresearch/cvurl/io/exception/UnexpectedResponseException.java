package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.model.Response;

/**
 * Thrown when a response with a status code that differs from the one specified in the
 * {@link coresearch.cvurl.io.request.Request#asObject(Class, int)} method and
 * {@link coresearch.cvurl.io.request.Request#asyncAsObject(Class, int)} method is returned.
 *
 * @since 0.9
 */
public class UnexpectedResponseException extends RuntimeException {

    private final Response<String> response;

    /**
     * Constructs a new exception with the specified detailed message and response.
     * The cause is not initialized and may subsequently be initialized by a call to the {@link #initCause} method.
     *
     * @param message - the detailed message is kept for later retrieval by the {@link #getMessage()} method.
     * @param response - the response with an unexpected status code.
     */
    public UnexpectedResponseException(String message, Response<String> response) {
        super(message);
        this.response = response;
    }

    public Response<String> getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "UnexpectedResponseException{" +
                "response=" + response +
                '}';
    }
}

