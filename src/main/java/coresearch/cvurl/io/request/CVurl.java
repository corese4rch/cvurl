package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.CVurlConfig;

import java.net.ProxySelector;
import java.net.URL;
import java.net.http.HttpClient;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;

/**
 * An HTTP Client.
 *
 * The central part of the lib. The instance of the {@link CVurl} class can be used to send HTTP requests and retrieve their responses.
 *
 * @since 0.9
 */
public class CVurl {

    private CVurlConfig cvurlConfig;

    /**
     * Creates an instance of the {@link CVurl} class
     * with the default mapper created by the {@link MapperFactory#createDefault()} method,
     * the default client created by the {@link HttpClient#newHttpClient()} method, and the request timeout is null.
     */
    public CVurl() {
        this.cvurlConfig = CVurlConfig.defaultConfiguration();
    }

    /**
     * Creates an instance of the {@link CVurl} class with the specified {@link CVurlConfig} and
     * the default mapper created by the {@link MapperFactory#createDefault()} method.
     *
     * @param cVurlConfig - the configuration
     */
    public CVurl(CVurlConfig cVurlConfig) {
        notNullParam(cVurlConfig);
        this.cvurlConfig = cVurlConfig;
    }

    /**
     * Creates an instance of the {@link CVurl} class with the specified {@link HttpClient} using
     * the default mapper created by the {@link MapperFactory#createDefault()} method, and
     * the request timeout is null.
     * In order for the {@link RequestBuilder#withProxy(CVurlProxy)} method to work, one should specify {@link CVurlProxySelector}
     * as a proxy selector for {@link HttpClient} using the {@link HttpClient.Builder#proxy(ProxySelector)} method.
     *
     * @param httpClient - the instance of the {@link HttpClient} class
     * @deprecated You should use {@link #CVurl()} if you want to create CVurl with default configuration,
     *             or {@link #CVurl(CVurlConfig)} to build customized {@link CVurl}.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public CVurl(HttpClient httpClient) {
        this.cvurlConfig = CVurlConfig.builder(httpClient).build();
    }

    /**
     * Returns the {@code cvurlConfig} value.
     */
    public CVurlConfig getCvurlConfig() {
        return cvurlConfig;
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP GET method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestBuilder<?> get(String url) {
        return createGetRequest(url);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP GET method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestBuilder<?> get(URL url) {
        return createGetRequest(url.toString());
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP POST method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder post(String url) {
        return createRequestWBody(url, HttpMethod.POST);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP POST method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder post(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.POST);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP PUT method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder put(String url) {
        return createRequestWBody(url, HttpMethod.PUT);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP PUT method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder put(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PUT);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP DELETE method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder delete(String url) {
        return createRequestWBody(url, HttpMethod.DELETE);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP DELETE method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder delete(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.DELETE);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP PATCH method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder patch(String url) {
        return createRequestWBody(url, HttpMethod.PATCH);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP PATCH method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder patch(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PATCH);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP HEAD method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder head(String url) {
        return createRequestWBody(url, HttpMethod.HEAD);
    }

    /**
     * Creates an instance of the {@link RequestBuilder} class with the specified URL for the HTTP HEAD method.
     *
     * @param url - the specified URL.
     * @return an instance of the {@link RequestBuilder} class
     */
    public RequestWithBodyBuilder head(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.HEAD);
    }

    private RequestBuilder<?> createGetRequest(String url) {
        return new RequestBuilder<>(url, HttpMethod.GET, cvurlConfig);
    }

    private RequestWithBodyBuilder createRequestWBody(String url, HttpMethod httpMethod) {
        return new RequestWithBodyBuilder(url, httpMethod, cvurlConfig);
    }
}
