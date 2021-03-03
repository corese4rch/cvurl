package coresearch.cvurl.io.request;

import java.net.Proxy;

public class CVurlProxy {

    private final Proxy.Type type;
    private final String host;
    private final int port;

    private CVurlProxy(Proxy.Type type, String host, int port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Proxy.Type getType() {
        return type;
    }

    public static CVurlProxy of(Proxy.Type type, String host, int port) {
        return new CVurlProxy(type, host, port);
    }

    public static CVurlProxy noProxy() {
        return new CVurlProxy(Proxy.Type.DIRECT, null, -1);
    }

}
