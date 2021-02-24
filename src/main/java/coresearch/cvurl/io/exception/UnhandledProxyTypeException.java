package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.request.CVurlProxyType;

public class UnhandledProxyTypeException extends RuntimeException{

    public UnhandledProxyTypeException(CVurlProxyType type) {
        super(String.format("Unhandled Proxy Type %s", type.name()));
    }

}
