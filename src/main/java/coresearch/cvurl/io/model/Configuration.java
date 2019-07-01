package coresearch.cvurl.io.model;

import coresearch.cvurl.io.util.HttpClientMode;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * Data class that holds configuration of {@link coresearch.cvurl.io.request.CVurl}
 */
public class Configuration {

    private static final int NO_PRIORITY = -1;

    private final Authenticator authenticator;
    private final Duration connectTimeout;
    private final CookieHandler cookieHandler;
    private final Executor executor;
    private final HttpClient.Redirect followRedirects;
    private final int priority;
    private final ProxySelector proxySelector;
    private final SSLContext sslContext;
    private final HttpClient.Version version;
    private final SSLParameters sslParameters;

    private final HttpClientMode httpClientMode;

    private final Duration requestTimeout;

    private Configuration(Authenticator authenticator, Duration connectTimeout, CookieHandler cookieHandler,
                          Executor executor, HttpClient.Redirect followRedirects, int priority, ProxySelector proxySelector,
                          SSLContext sslContext, HttpClient.Version version, SSLParameters sslParameters, Duration requestTimeout, HttpClientMode httpClientMode) {
        this.authenticator = authenticator;
        this.connectTimeout = connectTimeout;
        this.cookieHandler = cookieHandler;
        this.executor = executor;
        this.followRedirects = followRedirects;
        this.priority = priority;
        this.proxySelector = proxySelector;
        this.sslContext = sslContext;
        this.version = version;
        this.sslParameters = sslParameters;
        this.requestTimeout = requestTimeout;
        this.httpClientMode = httpClientMode;
    }

    /**
     * Creates {@link HttpClient} based on current configuration.
     *
     * @return new {@link HttpClient} build from this configuration
     */
    public HttpClient createHttpClient() {
        var builder = HttpClient.newBuilder();

        if (getConnectTimeout() != null) {
            builder.connectTimeout(getConnectTimeout());
        }
        if (getAuthenticator() != null) {
            builder.authenticator(getAuthenticator());
        }
        if (getCookieHandler() != null) {
            builder.cookieHandler(getCookieHandler());
        }
        if (getExecutor() != null) {
            builder.executor(getExecutor());
        }
        if (getPriority() != NO_PRIORITY) {
            builder.priority(getPriority());
        }
        if (getFollowRedirects() != null) {
            builder.followRedirects(getFollowRedirects());
        }
        if (getProxySelector() != null) {
            builder.proxy(getProxySelector());
        }
        if (getSslContext() != null) {
            builder.sslContext(getSslContext());
        }
        if (getSslParameters() != null) {
            builder.sslParameters(getSslParameters());
        }
        if (getVersion() != null) {
            builder.version(getVersion());
        }

        return builder.build();
    }

    /**
     * Creates {@link ConfigurationBuilder} used to build {@link Configuration} object.
     *
     * @return
     */
    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public ProxySelector getProxySelector() {
        return proxySelector;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public SSLParameters getSslParameters() {
        return sslParameters;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    public Executor getExecutor() {
        return executor;
    }

    public HttpClient.Redirect getFollowRedirects() {
        return followRedirects;
    }

    public int getPriority() {
        return priority;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public HttpClientMode getHttpClientMode() {
        return httpClientMode;
    }

    /**
     * Builder for {@link Configuration}.
     */
    public static class ConfigurationBuilder {
        private Authenticator authenticator;
        private Duration connectTimeout;
        private CookieHandler cookieHandler;
        private Executor executor;
        private HttpClient.Redirect followRedirects;
        private int priority = NO_PRIORITY;
        private ProxySelector proxySelector;
        private SSLContext sslContext;
        private HttpClient.Version version;
        private SSLParameters sslParameters;
        private Duration requestTimeout;
        private HttpClientMode httpClientMode = HttpClientMode.PROTOTYPE;

        public ConfigurationBuilder authenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        /**
         * Sets the connect timeout duration for this client.
         *
         * <p> In the case where a new connection needs to be established, if
         * the connection cannot be established within the given {@code
         * duration}, then {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
         * HttpClient::send} throws an {@link HttpConnectTimeoutException}, or
         * {@link HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler)
         * HttpClient::sendAsync} completes exceptionally with an
         * {@code HttpConnectTimeoutException}. If a new connection does not
         * need to be established, for example if a connection can be reused
         * from a previous request, then this timeout duration has no effect.
         *
         * @param connectTimeout the duration to allow the underlying connection to be
         *                       established
         * @return this builder
         */
        public ConfigurationBuilder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets a cookie handler.
         *
         * @param cookieHandler the cookie handler
         * @return this builder
         */
        public ConfigurationBuilder cookieHandler(CookieHandler cookieHandler) {
            this.cookieHandler = cookieHandler;
            return this;
        }

        /**
         * Sets the executor to be used for asynchronous and dependent tasks.
         *
         * <p> If this method is not invoked prior to {@linkplain #build()
         * building}, a default executor is created for each newly built {@code
         * HttpClient}.
         *
         * @param executor the Executor
         * @return this builder
         */
        public ConfigurationBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Specifies whether requests will automatically follow redirects issued
         * by the server.
         *
         * <p> If this method is not invoked prior to {@linkplain #build()
         * building}, then newly built clients will use a default redirection
         * policy of {@link HttpClient.Redirect#NEVER NEVER}.
         *
         * @param followRedirects the redirection policy
         * @return this builder
         */
        public ConfigurationBuilder followRedirects(HttpClient.Redirect followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * Sets the default priority for any HTTP/2 requests sent from this
         * client. The value provided must be between {@code 1} and {@code 256}
         * (inclusive).
         *
         * @param priority the priority weighting
         * @return this builder
         */
        public ConfigurationBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Sets a {@link java.net.ProxySelector}.
         *
         * @param proxySelector the ProxySelector
         * @return this builder
         * @apiNote {@link ProxySelector#of(InetSocketAddress) ProxySelector::of}
         * provides a {@code ProxySelector} which uses a single proxy for all
         * requests. The system-wide proxy selector can be retrieved by
         * {@link ProxySelector#getDefault()}.
         * @implNote If this method is not invoked prior to {@linkplain #build() building},
         * then newly built clients will use the {@linkplain
         * ProxySelector#getDefault() default proxy selector}, which is usually
         * adequate for client applications. The default proxy selector supports
         * a set of system properties</a> related to
         * <a href="{@docRoot}/java.base/java/net/doc-files/net-properties.html#Proxies">
         * proxy settings</a>. This default behavior can be disabled by
         * supplying an explicit proxy selector or
         * one returned by {@link ProxySelector#of(InetSocketAddress)
         * ProxySelector::of}, before {@linkplain #build() building}.
         */
        public ConfigurationBuilder proxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            return this;
        }

        /**
         * Sets an {@code SSLContext}.
         *
         * <p> If this method is not invoked prior to {@linkplain #build()
         * building}, then newly built clients will use the {@linkplain
         * SSLContext#getDefault() default context}, which is normally adequate
         * for client applications that do not need to specify protocols, or
         * require client authentication.
         *
         * @param sslContext the SSLContext
         * @return this builder
         */
        public ConfigurationBuilder sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        /**
         * Requests a specific HTTP protocol version where possible.
         * <p>
         * e<p> If this method is not invoked prior to {@linkplain #build()
         * building}, then newly built clients will prefer {@linkplain
         * HttpClient.Version#HTTP_2 HTTP/2}.
         *
         * <p> If set to {@linkplain HttpClient.Version#HTTP_2 HTTP/2}, then each request
         * will attempt to upgrade to HTTP/2. If the upgrade succeeds, then the
         * response to this request will use HTTP/2 and all subsequent requests
         * and responses to the same
         * <a href="https://tools.ietf.org/html/rfc6454#section-4">origin server</a>
         * will use HTTP/2. If the upgrade fails, then the response will be
         * handled using HTTP/1.1
         *
         * @param version the requested HTTP protocol version
         * @return this builder
         * @implNote Constraints may also affect the selection of protocol version.
         * For example, if HTTP/2 is requested through a proxy, and if the implementation
         * does not support this mode, then HTTP/1.1 may be used
         */
        public ConfigurationBuilder version(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        /**
         * Sets an {@code SSLParameters}.
         *
         * <p> If this method is not invoked prior to {@linkplain #build()
         * building}, then newly built clients will use a default,
         * implementation specific, set of parameters.
         *
         * <p> Some parameters which are used internally by the HTTP Client
         * implementation (such as the application protocol list) should not be
         * set by callers, as they may be ignored. The contents of the given
         * object are copied.
         *
         * @param sslParameters the SSLParameters
         * @return this builder
         */
        public ConfigurationBuilder sslParameters(SSLParameters sslParameters) {
            this.sslParameters = sslParameters;
            return this;
        }

        /**
         * Sets a global timeout for requests created by {@link coresearch.cvurl.io.request.CVurl}
         * from this configuration.The effect
         * of not setting a timeout is the same as setting an infinite Duration, ie.
         * block forever.
         *
         * @param requestTimeout the timeout duration
         * @return this builder
         */
        public ConfigurationBuilder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * Sets {@link HttpClientMode}. If mode is set to PROTOTYPE - new {@link HttpClient}
         * will be create for each created {@link coresearch.cvurl.io.request.CVurl}. If mode
         * is set to SINGLETONE - then all instances of {@link coresearch.cvurl.io.request.CVurl}
         * that created from config with this mode will use the same {@link HttpClient}
         * configured on the first {@link coresearch.cvurl.io.request.CVurl} creation.
         *
         * @param httpClientMode
         * @return this builder
         */
        public ConfigurationBuilder httpClientMode(HttpClientMode httpClientMode) {
            this.httpClientMode = httpClientMode;
            return this;
        }

        /**
         * Builds new configuration.
         *
         * @return new configuration
         */
        public Configuration build() {
            return new Configuration(authenticator, connectTimeout, cookieHandler, executor,
                    followRedirects, priority, proxySelector, sslContext, version, sslParameters, requestTimeout, httpClientMode);
        }
    }
}