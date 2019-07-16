package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.model.Response;

/**
 * Thrown when response with status code that differs from the one specified in
 * {@link coresearch.cvurl.io.request.Request#asObject(Class, int)}
 * and {@link coresearch.cvurl.io.request.Request#asyncAsObject(Class, int)} is returned.
 */
public class UnexpectedResponseException extends RuntimeException {

    /**
     * Response with unexpected status code.
     */
    private final Response<String> response;

    /**
     * Constructs a new exception with the specified detail message and response.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message  the detail message. The detail message is saved for
     *                 later retrieval by the {@link #getMessage()} method.
     * @param response the response with unexpected status code.
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

