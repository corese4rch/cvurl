package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.CVurlConfig;

import java.net.ProxySelector;
import java.net.URL;
import java.net.http.HttpClient;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;

/**
 * Central part of the lib. Used to initiate {@link Request} creation with proper {@link GenericMapper} and
 * {@link HttpClient}.
 */
public class CVurl {

    private CVurlConfig cvurlConfig;

    /**
     * Creates CVurl with default mapper created by {@link MapperFactory#createDefault()}, default {@link HttpClient} created
     * by {@link HttpClient#newHttpClient()} and request timeout set to null.
     */
    public CVurl() {
        this.cvurlConfig = CVurlConfig.defaultConfiguration();
    }

    /**
     * Creates CVurl with specified {@link CVurlConfig} and
     * default mapper created by {@link MapperFactory#createDefault()}.
     *
     * @param cvurlConfig configuration with which CVurl will be created.
     */
    public CVurl(CVurlConfig cvurlConfig) {
        notNullParam(cvurlConfig);
        this.cvurlConfig = cvurlConfig;
    }

    /**
     * Creates CVurl with specified {@link HttpClient} using
     * default mapper created by {@link MapperFactory#createDefault()} and
     * request timeout set to null.
     * In order for {@link RequestBuilder#withProxy(CVurlProxy)} to work one should specify {@link CVurlProxySelector}
     * as proxy selector for {@link HttpClient} using {@link HttpClient.Builder#proxy(ProxySelector)}
     *
     * @param httpClient httpClient with which CVurl will be created.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public CVurl(HttpClient httpClient) {
        this.cvurlConfig = CVurlConfig.builder(httpClient).build();
    }

    public CVurlConfig getCvurlConfig() {
        return cvurlConfig;
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.GET.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestBuilder<?> get(String url) {
        return createGetRequest(url);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.GET.
     *
     * @param url specified url.
     * @return RequestBuilder
     */

    public RequestBuilder<?> get(URL url) {
        return createGetRequest(url.toString());
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.POST.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder post(String url) {
        return createRequestWBody(url, HttpMethod.POST);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.POST.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder post(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.POST);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PUT.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder put(String url) {
        return createRequestWBody(url, HttpMethod.PUT);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PUT.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder put(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PUT);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.DELETE.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder delete(String url) {
        return createRequestWBody(url, HttpMethod.DELETE);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.DELETE.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder delete(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.DELETE);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PATCH.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder patch(String url) {
        return createRequestWBody(url, HttpMethod.PATCH);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PATCH.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder patch(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PATCH);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.HEAD.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder head(String url) {
        return createRequestWBody(url, HttpMethod.HEAD);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.HEAD.
     *
     * @param url specified url.
     * @return RequestBuilder
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
