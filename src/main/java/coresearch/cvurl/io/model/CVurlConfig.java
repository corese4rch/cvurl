package coresearch.cvurl.io.model;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.internal.configuration.RequestConfigurer;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.request.proxy.CVurlProxySelector;
import coresearch.cvurl.io.internal.util.HttpClientSingleton;

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
import java.util.Optional;
import java.util.concurrent.Executor;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;
import static coresearch.cvurl.io.internal.util.Validation.notNullParams;

/**
 * The class that contains the configuration for the HTTP client.
 *
 * @since 0.9
 */
public class CVurlConfig {

    private final HttpClient httpClient;

    private final GenericMapper genericMapper;

    private final HttpClientMode httpClientMode;

    private final RequestConfiguration globalRequestConfiguration;

    private CVurlConfig(HttpClient httpClient, GenericMapper genericMapper, HttpClientMode httpClientMode,
                        RequestConfiguration globalRequestConfiguration) {
        notNullParams(httpClient, genericMapper, globalRequestConfiguration);

        this.httpClient = httpClient;
        this.genericMapper = genericMapper;
        this.globalRequestConfiguration = globalRequestConfiguration;
        this.httpClientMode = httpClientMode;
    }

    public CVurlConfig() {
        this.httpClient = makeDefaultHttpClientWithCVurlProxySelector();
        this.genericMapper = MapperFactory.createDefault();
        this.globalRequestConfiguration = RequestConfiguration.defaultConfiguration();
        this.httpClientMode = HttpClientMode.PROTOTYPE;
    }

    /**
     * Creates an instance of the {@link ConfigurationWithClientPropertiesBuilder} class used to build the {@link CVurlConfig} object.
     *
     * @return an instance of the {@link ConfigurationWithClientPropertiesBuilder} class
     */
    public static ConfigurationWithClientPropertiesBuilder builder() {
        return new ConfigurationWithClientPropertiesBuilder();
    }

    /**
     * Creates an instance of the {@link ConfigurationBuilder} class with
     * the predefined {@link HttpClient} instance used to build the {@link CVurlConfig} object.
     *
     * @return an instance of the {@link ConfigurationBuilder} class
     * @deprecated use the {@link CVurlConfig#builderWithDefaultHttpClient()} method instead.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static ConfigurationBuilder builder(HttpClient httpClient) {
        return new ConfigurationBuilder(httpClient);
    }

    /**
     * Creates an instance of the {@link ConfigurationBuilder} class with
     * the default {@link HttpClient} instance used to build the {@link CVurlConfig} object.
     *
     * @return an instance of the {@link ConfigurationBuilder} class
     */
    public static ConfigurationBuilder builderWithDefaultHttpClient() {
        return new ConfigurationBuilder(makeDefaultHttpClientWithCVurlProxySelector());
    }

    /**
     * Creates an instance of the {@link CVurlConfig} class with default properties.
     *
     * @return an instance of the {@link CVurlConfig} class
     */
    public static CVurlConfig defaultConfiguration() {
        return new CVurlConfig();
    }

    /**
     * Creates an instance of the {@link ConfigurationBuilder} class used to build the {@link CVurlConfig} instance.
     *
     * @return an instance of the {@link ConfigurationBuilder} class
     */
    public ConfigurationBuilder preconfiguredBuilder() {
        return new ConfigurationBuilder(getHttpClient())
                .genericMapper(getGenericMapper())
                .requestTimeout(getGlobalRequestConfiguration().getRequestTimeout().orElse(null));
    }

    /**
     * Returns the {@code httpClient} value.
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Returns the {@link HttpClient#proxy()} value.
     */
    public Optional<ProxySelector> getProxySelector() {
        return httpClient.proxy();
    }

    /**
     * Returns the {@link HttpClient#sslContext()} value.
     */
    public SSLContext getSslContext() {
        return httpClient.sslContext();
    }

    /**
     * Returns the {@link HttpClient#version()} value.
     */
    public HttpClient.Version getVersion() {
        return httpClient.version();
    }

    /**
     * Returns the {@link HttpClient#sslParameters()} value.
     */
    public SSLParameters getSslParameters() {
        return httpClient.sslParameters();
    }

    /**
     * Returns the {@link HttpClient#authenticator()} value.
     */
    public Optional<Authenticator> getAuthenticator() {
        return httpClient.authenticator();
    }

    /**
     * Returns the {@link HttpClient#connectTimeout()} value.
     */
    public Optional<Duration> getConnectTimeout() {
        return httpClient.connectTimeout();
    }

    /**
     * Returns the {@link HttpClient#cookieHandler()} value.
     */
    public Optional<CookieHandler> getCookieHandler() {
        return httpClient.cookieHandler();
    }

    /**
     * Returns the {@link HttpClient#executor()} value.
     */
    public Optional<Executor> getExecutor() {
        return httpClient.executor();
    }

    /**
     * Returns the {@link HttpClient#followRedirects()} value.
     */
    public HttpClient.Redirect getFollowRedirects() {
        return httpClient.followRedirects();
    }

    /**
     * Returns the {@code genericMapper} value.
     */
    public GenericMapper getGenericMapper() {
        return genericMapper;
    }

    /**
     * Returns the {@code globalRequestConfiguration} value.
     */
    public RequestConfiguration getGlobalRequestConfiguration() {
        return globalRequestConfiguration;
    }

    /**
     * Returns the {@code httpClientMode} value.
     */
    public HttpClientMode getHttpClientMode() {
        return httpClientMode;
    }

    /**
     * Sets the value of the {@code logEnabled} field.
     * @param enabled - the value of the logEnabled field
     * @deprecated use the {@link CVurlConfig#setLogEnabled(boolean)} method instead
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public void setIsLogEnable(boolean enabled) {
        this.getGlobalRequestConfiguration().setLogEnabled(enabled);
    }

    /**
     * Sets the value of the {@code logEnabled} field.
     * @param isEnabled - the value of the logEnabled field
     */
    public void setLogEnabled(boolean isEnabled) {
        this.getGlobalRequestConfiguration().setLogEnabled(isEnabled);
    }

    /**
     * Creates an instance of the {@link HttpClient} class with the default settings and the CVurl proxy selector.
     * @return an instance of the {@link HttpClient} class with the CVurl proxy selector
     */
    private static HttpClient makeDefaultHttpClientWithCVurlProxySelector() {
        return HttpClient.newBuilder()
                .proxy(new CVurlProxySelector())
                .build();
    }

    /**
     * A mutable builder for the {@link CVurlConfig} class.
     *
     * @since 0.9
     */
    public static class ConfigurationBuilder<T extends ConfigurationBuilder<T>> implements RequestConfigurer<ConfigurationBuilder<T>> {

        private GenericMapper genericMapper;
        private HttpClient httpClient;
        private HttpClientMode httpClientMode = HttpClientMode.PROTOTYPE;

        private final RequestConfiguration.Builder requestConfigurationBuilder = RequestConfiguration.builder();

        private ConfigurationBuilder() {
        }

        private ConfigurationBuilder(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        /**
         * Sets the value of the {@code genericMapper} field. The value must not be null.
         * @param genericMapper - the value of the genericMapper field
         * @return the builder
         */
        @SuppressWarnings("unchecked")
        public T genericMapper(GenericMapper genericMapper) {
            this.genericMapper = notNullParam(genericMapper);
            return (T) this;
        }

        /**
         * Sets the value of the {@code httpClientMode} field. The value must not be null.
         * @param httpClientMode - the value of the httpClientMode field
         * @return the builder
         */
        @SuppressWarnings("unchecked")
        public T httpClientMode(HttpClientMode httpClientMode) {
            this.httpClientMode = notNullParam(httpClientMode);
            return (T) this;
        }

        /**
         * Sets the value of the global timeout.
         * The effect of no timeout is the same as setting the infinite duration.
         *
         * @param requestTimeout - the timeout duration
         * @return the builder
         */
        @Override
        @SuppressWarnings("unchecked")
        public T requestTimeout(Duration requestTimeout) {
            this.requestConfigurationBuilder.requestTimeout(requestTimeout);
            return (T) this;
        }

        /**
         * Sets a property that determines whether decompression should be applied to the response body,
         * and an appropriate header should be added to the request.
         *
         * @param acceptCompressed - the value of the property
         * @return the builder
         */
        @Override
        @SuppressWarnings("unchecked")
        public T acceptCompressed(boolean acceptCompressed) {
            this.requestConfigurationBuilder.acceptCompressed(acceptCompressed);
            return (T) this;
        }

        /**
         * Sets the value of the global logging. If enabled, then each request is logged.
         *
         * @param logEnabled -the value of the log enabled flag
         * @return the builder
         */
        @Override
        @SuppressWarnings("unchecked")
        public T logEnabled(boolean logEnabled) {
            this.requestConfigurationBuilder.logEnabled(logEnabled);
            return (T) this;
        }

        protected HttpClient getHttpClient() {
            return this.httpClient;
        }

        /**
         * Builds the CVurl configuration.
         * @return an instance of the {@link CVurlConfig} class
         */
        public CVurlConfig build() {
            if (genericMapper == null) {
                genericMapper = MapperFactory.createDefault();
            }

            var client = httpClientMode == HttpClientMode.PROTOTYPE ?
                    this.getHttpClient() : HttpClientSingleton.getClient(this.getHttpClient());

            return new CVurlConfig(client, genericMapper, httpClientMode, requestConfigurationBuilder.build());
        }
    }

    /**
     * Extended builder for the {@link CVurlConfig} class.
     */
    public static class ConfigurationWithClientPropertiesBuilder
            extends ConfigurationBuilder<ConfigurationWithClientPropertiesBuilder> {

        private static final int NO_PRIORITY = -1;

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

        public ConfigurationWithClientPropertiesBuilder authenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        /**
         * Sets the duration of the connection timeout for this client.
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
         * @param connectTimeout - the duration to allow the underlying connection to be
         *                       established
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets a cookie handler.
         *
         * @param cookieHandler - the cookie handler
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder cookieHandler(CookieHandler cookieHandler) {
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
         * @param executor - the executor
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder executor(Executor executor) {
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
         * @param followRedirects - the redirection policy
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder followRedirects(HttpClient.Redirect followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * Sets the default priority for any HTTP/2 requests sent from this
         * client. The value provided must be between {@code 1} and {@code 256}
         * (inclusive).
         *
         * @param priority - the priority value
         * @return this builder
         */
        public ConfigurationWithClientPropertiesBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Sets a {@link ProxySelector} instance.
         *
         * @param proxySelector - the instance of the {@link ProxySelector} class
         * @return the builder
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
        public ConfigurationWithClientPropertiesBuilder proxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            return this;
        }

        /**
         * Sets an instance of the {@link SSLContext} class.
         *
         * <p> If this method is not invoked prior to {@linkplain #build()
         * building}, then newly built clients will use the {@linkplain
         * SSLContext#getDefault() default context}, which is normally adequate
         * for client applications that do not need to specify protocols, or
         * require client authentication.
         *
         * @param sslContext - the instance of the {@link SSLContext} class
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder sslContext(SSLContext sslContext) {
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
         * @param version - the requested HTTP protocol version
         * @return the builder
         * @implNote Constraints may also affect the selection of protocol version.
         * For example, if HTTP/2 is requested through a proxy, and if the implementation
         * does not support this mode, then HTTP/1.1 may be used
         */
        public ConfigurationWithClientPropertiesBuilder version(HttpClient.Version version) {
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
         * @param sslParameters - the instance of the {@link SSLParameters} class
         * @return the builder
         */
        public ConfigurationWithClientPropertiesBuilder sslParameters(SSLParameters sslParameters) {
            this.sslParameters = sslParameters;
            return this;
        }

        @Override
        protected HttpClient getHttpClient() {
            var builder = HttpClient.newBuilder();

            if (connectTimeout != null) {
                builder.connectTimeout(connectTimeout);
            }

            if (authenticator != null) {
                builder.authenticator(authenticator);
            }

            if (cookieHandler != null) {
                builder.cookieHandler(cookieHandler);
            }

            if (executor != null) {
                builder.executor(executor);
            }

            if (priority != NO_PRIORITY) {
                builder.priority(priority);
            }

            if (followRedirects != null) {
                builder.followRedirects(followRedirects);
            }

            builder.proxy(new CVurlProxySelector(proxySelector));

            if (sslContext != null) {
                builder.sslContext(sslContext);
            }

            if (sslParameters != null) {
                builder.sslParameters(sslParameters);
            }

            if (version != null) {
                builder.version(version);
            }

            return builder.build();
        }
    }
}