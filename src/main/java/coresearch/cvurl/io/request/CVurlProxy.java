package coresearch.cvurl.io.request;

import java.net.Proxy;

/**
 * The helper class for proxy configuration.
 *
 * @since 1.5
 */
public class CVurlProxy {

    private final Proxy.Type type;
    private final String host;
    private final int port;

    private CVurlProxy(Proxy.Type type, String host, int port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    /**
     * Returns the {@code host} value.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the {@code port} value.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the {@code type} value.
     */
    public Proxy.Type getType() {
        return type;
    }

    /**
     * Creates a new instance of the {@link CVurlProxy} class
     *
     * @param type - the type of the proxy
     * @param host - the proxy host
     * @param port - the proxy port
     * @return an instance of the {@link CVurlProxy} class
     */
    public static CVurlProxy of(Proxy.Type type, String host, int port) {
        return new CVurlProxy(type, host, port);
    }

    /**
     * Creates a new instance of the {@link CVurlProxy} class. Can be used when no proxy is needed.
     *
     * @return an instance of the {@link CVurlProxy} class
     */
    public static CVurlProxy noProxy() {
        return new CVurlProxy(Proxy.Type.DIRECT, null, -1);
    }

}
