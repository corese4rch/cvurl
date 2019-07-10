package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.util.HttpMethod;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * Builder used to build {@link Request}
 *
 * @param <T>
 */
public class RequestBuilder<T extends RequestBuilder<T>> {

    protected GenericMapper genericMapper;
    protected HttpMethod method;
    protected HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.noBody();

    private String uri;
    private HttpClient httpClient;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Optional<Duration> timeout = Optional.empty();

    RequestBuilder(String uri, HttpMethod method, GenericMapper genericMapper, HttpClient httpClient) {
        this.method = method;
        this.uri = uri;
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
    }

    /**
     * Add request header.
     *
     * @param key   header key
     * @param value header value
     * @return this builder
     */
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
    public T queryParams(Map<String, String> queryParams) {
        this.queryParams.putAll(queryParams);
        return (T) this;
    }

    /**
     * Sets request timeout. Overlaps global timeout set for {@link CVurl}
     *
     * @param timeout request timeout
     * @return this builder
     */
    public T timeout(Duration timeout) {
        this.timeout = Optional.ofNullable(timeout);
        return (T) this;
    }

    /**
     * Builds new {@link Request}.
     *
     * @return new {@link Request}
     */
    public Request build() {
        return new Request(setUpHttpRequestBuilder().build(), httpClient, genericMapper);
    }

    private HttpRequest.Builder setUpHttpRequestBuilder() {
        var builder = HttpRequest.newBuilder()
                .uri(prepareURI())
                .method(method.name(), bodyPublisher);

        timeout.ifPresent(builder::timeout);

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
}
