package coreserech.cvurl.io.exception;

import coreserech.cvurl.io.model.Response;

public class UnexpectedResponseException extends RuntimeException {

    private final Response<String> response;

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

