package coreserech.cvurl.io.model;

import coreserech.cvurl.io.util.HttpClientMode;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;

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

        public ConfigurationBuilder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public ConfigurationBuilder cookieHandler(CookieHandler cookieHandler) {
            this.cookieHandler = cookieHandler;
            return this;
        }

        public ConfigurationBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public ConfigurationBuilder followRedirects(HttpClient.Redirect followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public ConfigurationBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public ConfigurationBuilder proxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            return this;
        }

        public ConfigurationBuilder sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public ConfigurationBuilder version(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        public ConfigurationBuilder sslParameters(SSLParameters sslParameters) {
            this.sslParameters = sslParameters;
            return this;
        }

        public ConfigurationBuilder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public ConfigurationBuilder httpClientMode(HttpClientMode httpClientMode) {
            this.httpClientMode = httpClientMode;
            return this;
        }

        public Configuration build() {
            return new Configuration(authenticator, connectTimeout, cookieHandler, executor,
                    followRedirects, priority, proxySelector, sslContext, version, sslParameters, requestTimeout, httpClientMode);
        }
    }
}