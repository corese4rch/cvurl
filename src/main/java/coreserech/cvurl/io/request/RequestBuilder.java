package coreserech.cvurl.io.request;

import coreserech.cvurl.io.mapper.GenericMapper;
import coreserech.cvurl.io.util.HttpMethod;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class RequestBuilder<T extends RequestBuilder<T>> {

    protected GenericMapper genericMapper;
    protected HttpMethod method;
    private String uri;
    private HttpClient httpClient;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Duration timeout = null;

    RequestBuilder(String uri, HttpMethod method, GenericMapper genericMapper, HttpClient httpClient) {
        this.method = method;
        this.uri = uri;
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
    }

    public T header(String key, String value) {
        this.headers.put(key, value);
        return (T) this;
    }

    public T headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return (T) this;
    }

    public T queryParam(String name, String value) {
        this.queryParams.put(name, value);
        return (T) this;
    }

    public T queryParams(Map<String, String> queryParams) {
        this.queryParams.putAll(queryParams);
        return (T) this;
    }

    public T timeout(Duration timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    public Request build() {
        return new Request(setUpHttpRequestBuilder().build(), httpClient, genericMapper);
    }

    protected HttpRequest.Builder setUpHttpRequestBuilder() {
        var builder = HttpRequest.newBuilder()
                .uri(prepareURI())
                .method(method.name(), HttpRequest.BodyPublishers.noBody());

        if (timeout != null) {
            builder.timeout(timeout);
        }

        headers.forEach(builder::header);

        return builder;
    }

    private URI prepareURI() {
        return queryParams.isEmpty() ? URI.create(uri) :
                URI.create(uri +
                        this.queryParams.entrySet().stream()
                                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                                .collect(joining("&", "?", "")));
    }

    private String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
