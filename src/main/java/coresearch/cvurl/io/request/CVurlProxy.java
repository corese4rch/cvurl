package coresearch.cvurl.io.request;

public class CVurlProxy {

    private final Type type;
    private final String host;
    private final int port;

    private CVurlProxy(Type type, String host, int port) {
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

    public Type getType() {
        return type;
    }

    public static CVurlProxy of(Type type, String host, int port) {
        return new CVurlProxy(type, host, port);
    }

    public static CVurlProxy noProxy() {
        return new CVurlProxy(Type.DIRECT, null, -1);
    }

    public enum Type {
        HTTP,
        SOCKS,
        DIRECT,
        ;
    }
}
