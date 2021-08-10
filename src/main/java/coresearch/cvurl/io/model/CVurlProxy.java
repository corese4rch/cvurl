package coresearch.cvurl.io.model;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;

/**
 * The helper class for proxy configuration.
 *
 * @since 1.5
 */
public class CVurlProxy {

    private final String host;
    private final int port;

    private CVurlProxy(String host, int port) {
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
     * Creates a new instance of the {@link CVurlProxy} class
     *
     * @param host - the proxy host
     * @param port - the proxy port
     * @return an instance of the {@link CVurlProxy} class
     */
    public static CVurlProxy of(String host, int port) {
        notNullParam(host, "host");
        notNullParam(port, "port");

        return new CVurlProxy(host, port);
    }

    /**
     * Creates a new instance of the {@link CVurlProxy} class. Can be used when no proxy is needed.
     *
     * @return an instance of the {@link CVurlProxy} class
     */
    public static CVurlProxy noProxy() {
        return new CVurlProxy(null, -1);
    }

}
