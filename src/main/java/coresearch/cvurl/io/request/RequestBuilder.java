package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpContentEncoding;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.internal.configuration.RequestConfigurer;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;

import static java.util.stream.Collectors.joining;

/**
 * Builder used to build {@link Request}
 *
 * @param <T>
 */
public class RequestBuilder<T extends RequestBuilder<T>> implements Request, RequestConfigurer<RequestBuilder> {

    protected final CVurlConfig cvurlConfig;
    protected final RequestConfiguration.Builder requestConfigurationBuilder;

    protected HttpMethod method;
    protected HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.noBody();

    private String uri;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    RequestBuilder(String uri, HttpMethod method, CVurlConfig cvurlConfig) {
        this.method = method;
        this.uri = uri;
        this.cvurlConfig = cvurlConfig;
        this.requestConfigurationBuilder = cvurlConfig.getGlobalRequestConfiguration().preconfiguredBuilder();
    }

    /**
     * Add request header.
     *
     * @param key   header key
     * @param value header value
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public T header(String key, String value) {
        this.headers.put(key, value);
        return (T) this;
    }

    /**
     * Add request headers.
     *
     * @param headers headers name/value map
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public T headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return (T) this;
    }

    /**
     * Adds query parameter.
     *
     * @param name  query parameter name
     * @param value query parameter value
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public T queryParam(String name, String value) {
        this.queryParams.put(name, value);
        return (T) this;
    }

    /**
     * Adds query parameters.
     *
     * @param queryParams query parameters name/value map
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public T queryParams(Map<String, String> queryParams) {
        this.queryParams.putAll(queryParams);
        return (T) this;
    }

    /**
     * Sets request timeout. Overlaps global timeout set for {@link CVurl}
     *
     * @param timeout request timeout
     * @return this builder
     * @deprecated Use {@link #requestTimeout(Duration)} instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated(since = "1.2", forRemoval = true)
    public T timeout(Duration timeout) {
        this.requestConfigurationBuilder.requestTimeout(timeout);
        return (T) this;
    }

    /**
     * Sets whether this client should accept compressed response body.
     *
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public T acceptCompressed() {
        this.requestConfigurationBuilder.acceptCompressed(true);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T requestTimeout(Duration timeout) {
        this.requestConfigurationBuilder.requestTimeout(timeout);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T acceptCompressed(boolean acceptCompressed) {
        this.requestConfigurationBuilder.acceptCompressed(acceptCompressed);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T logEnabled(boolean logEnabled) {
        this.requestConfigurationBuilder.logEnabled(logEnabled);
        return (T) this;
    }

    /**
     * Builds new {@link Request}.
     *
     * @return new {@link Request}
     */
    public Request create() {
        RequestConfiguration requestConfiguration = requestConfigurationBuilder.build();
        return new CVurlRequest(setUpHttpRequestBuilder(requestConfiguration).build(), cvurlConfig, requestConfiguration);
    }

    private HttpRequest.Builder setUpHttpRequestBuilder(RequestConfiguration requestConfiguration) {
        var builder = HttpRequest.newBuilder()
                .uri(prepareURI())
                .method(method.name(), bodyPublisher);

        if (requestConfiguration.isAcceptCompressed()) {
            this.header(HttpHeader.ACCEPT_ENCODING, HttpContentEncoding.GZIP);
        }

        requestConfiguration.getRequestTimeout().ifPresent(builder::timeout);

        headers.forEach(builder::header);

        return builder;
    }

    private URI prepareURI() {
        return queryParams.isEmpty() ? URI.create(uri) :
                URI.create(uri +
                        this.queryParams.entrySet().stream()
                                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                                .collect(joiningWithUri(uri)));
    }

    private Collector<CharSequence, ?, String> joiningWithUri(String uri) {
        return uri.contains("?") ?
                joining("&", "&", "") :
                joining("&", "?", "");
    }

    private String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    @Override
    public <U> CompletableFuture<U> asyncAsObject(Class<U> type, int statusCode) {
        return create().asyncAsObject(type, statusCode);
    }

    @Override
    public <U> CompletableFuture<U> asyncAsObject(BodyType<U> type, int statusCode) {
        return create().asyncAsObject(type, statusCode);
    }

    @Override
    public <U> CompletableFuture<U> asyncAsObject(Class<U> type) {
        return create().asyncAsObject(type);
    }

    @Override
    public <U> CompletableFuture<U> asyncAsObject(BodyType<U> type) {
        return create().asyncAsObject(type);
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString() {
        return create().asyncAsString();
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph) {
        return create().asyncAsString(pph);
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return create().asyncAsStream();
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph) {
        return create().asyncAsStream(pph);
    }

    @Override
    public <U> CompletableFuture<Response<U>> asyncAs(HttpResponse.BodyHandler<U> bodyHandler) {
        return create().asyncAs(bodyHandler);
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph) {
        return create().asyncAs(bodyHandler, pph);
    }

    @Override
    public <U> Optional<U> asObject(Class<U> type, int statusCode) {
        return create().asObject(type, statusCode);
    }

    @Override
    public <U> Optional<U> asObject(BodyType<U> type, int statusCode) {
        return create().asObject(type, statusCode);
    }

    @Override
    public <U> U asObject(Class<U> type) {
        return create().asObject(type);
    }

    @Override
    public <T> T asObject(BodyType<T> type) {
        return create().asObject(type);
    }

    @Override
    public Optional<Response<String>> asString() {
        return create().asString();
    }

    @Override
    public Optional<Response<InputStream>> asStream() {
        return create().asStream();
    }

    @Override
    public <U> Optional<Response<U>> as(HttpResponse.BodyHandler<U> bodyHandler) {
        return create().as(bodyHandler);
    }
}
