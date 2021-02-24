package coresearch.cvurl.io.request;

import java.net.Proxy;

public enum CVurlProxyType {
    HTTP(Proxy.Type.HTTP),
    SOCKS(Proxy.Type.SOCKS),
    DIRECT(Proxy.Type.DIRECT),
    ;

    private final Proxy.Type javaProxyType;

    CVurlProxyType(Proxy.Type javaProxyType) {
        this.javaProxyType = javaProxyType;
    }

    public Proxy.Type getJavaProxyType() {
        return javaProxyType;
    }
}
