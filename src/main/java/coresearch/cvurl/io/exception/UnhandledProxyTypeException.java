package coresearch.cvurl.io.exception;

import coresearch.cvurl.io.request.CVurlProxy;

public class UnhandledProxyTypeException extends RuntimeException{

    public UnhandledProxyTypeException(CVurlProxy.Type type) {
        super(String.format("Unhandled Proxy Type %s", type.name()));
    }

}
