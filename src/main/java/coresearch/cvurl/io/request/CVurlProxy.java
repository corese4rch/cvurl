package coresearch.cvurl.io.request;

public class CVurlProxy {

    private final CVurlProxyType type;
    private final String host;
    private final int port;

    private CVurlProxy(CVurlProxyType type, String host, int port) {
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

    public CVurlProxyType getType() {
        return type;
    }

    public static CVurlProxy of(CVurlProxyType type, String host, int port) {
        return new CVurlProxy(type, host, port);
    }

    public static CVurlProxy noProxy() {
        return new CVurlProxy(CVurlProxyType.DIRECT, null, -1);
    }

}
