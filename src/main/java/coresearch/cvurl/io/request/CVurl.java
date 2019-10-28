package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.Configuration;

import java.net.URL;
import java.net.http.HttpClient;
import java.time.Duration;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;

/**
 * Central part of the lib. Used to initiate {@link Request} creation with proper {@link GenericMapper} and
 * {@link HttpClient}.
 */
public class CVurl {

    private Configuration configuration;

    /**
     * Creates CVurl with default mapper created by {@link MapperFactory#createDefault()}, default {@link HttpClient} created
     * by {@link HttpClient#newHttpClient()} and request timeout set to null.
     */
    public CVurl() {
        this.configuration = Configuration.defaultConfiguration();
    }

    /**
     * Creates CVurl with specified {@link Configuration} and
     * default mapper created by {@link MapperFactory#createDefault()}.
     *
     * @param configuration configuration with which CVurl will be created.
     */
    public CVurl(Configuration configuration) {
        notNullParam(configuration);
        this.configuration = configuration;
    }

    /**
     * Creates CVurl with specified {@link HttpClient} using
     * default mapper created by {@link MapperFactory#createDefault()} and
     * request timeout set to null.
     *
     * @param httpClient httpClient with which CVurl will be created.
     */
    public CVurl(HttpClient httpClient) {
        this.configuration = Configuration.builder(httpClient).build();
    }

    /**
     * Creates CVurl with specified {@link GenericMapper}, default {@link HttpClient} created
     * by {@link HttpClient#newHttpClient()} and request timeout set to null.
     * Deprecated, you should use {@link #CVurl()} if you want to create CVurl with default configuration,
     * or {@link #CVurl(Configuration)} to build customized CVurl, or {@link #CVurl(HttpClient)} if
     * you want to create {@link CVurl} with given HttpClient.
     *
     * @param genericMapper mapper with which CVurl will be created.
     */
    @Deprecated
    public CVurl(GenericMapper genericMapper) {
        this.configuration = Configuration.builder().genericMapper(genericMapper).build();
    }

    /**
     * Creates CVurl with specified {@link GenericMapper} and {@link Configuration}
     * Deprecated, you should use {@link #CVurl()} if you want to create CVurl with default configuration,
     * or {@link #CVurl(Configuration)} to build customized CVurl, or {@link #CVurl(HttpClient)} if
     * you want to create {@link CVurl} with given HttpClient.
     *
     * @param genericMapper mapper with which CVurl will be created.
     * @param configuration configuration with which CVurl will be created.
     */
    @Deprecated
    public CVurl(GenericMapper genericMapper, Configuration configuration) {
        notNullParam(configuration);
        this.configuration = configuration.preconfiguredBuilder().genericMapper(genericMapper).build();
    }

    /**
     * Creates CVurl with specified {@link HttpClient} and request timeout using
     * default mapper created by {@link MapperFactory#createDefault()}.
     * Deprecated, you should use {@link #CVurl()} if you want to create CVurl with default configuration,
     * or {@link #CVurl(Configuration)} to build customized CVurl, or {@link #CVurl(HttpClient)} if
     * you want to create {@link CVurl} with given HttpClient.
     *
     * @param httpClient     httpClient with which CVurl will be created.
     * @param requestTimeout requestTimeout with which CVurl will be created.
     */
    @Deprecated
    public CVurl(HttpClient httpClient, Duration requestTimeout) {
        this.configuration = Configuration.builder(httpClient).requestTimeout(requestTimeout).build();
    }

    /**
     * Creates CVurl with specified {@link HttpClient} and {@link GenericMapper}.
     * Timeout duration is set to null.
     * Deprecated, you should use {@link #CVurl()} if you want to create CVurl with default configuration,
     * or {@link #CVurl(Configuration)} to build customized CVurl, or {@link #CVurl(HttpClient)} if
     * you want to create {@link CVurl} with given HttpClient.
     *
     * @param genericMapper mapper with which CVurl will be created.
     * @param httpClient    httpClient with which CVurl will be created.
     */
    @Deprecated
    public CVurl(GenericMapper genericMapper, HttpClient httpClient) {
        this.configuration = Configuration.builder(httpClient).genericMapper(genericMapper).build();
    }

    /**
     * Creates CVurl with specified {@link HttpClient}, {@link GenericMapper} and
     * timeout duration.
     * Deprecated, you should use {@link #CVurl()} if you want to create CVurl with default configuration,
     * or {@link #CVurl(Configuration)} to build customized CVurl, or {@link #CVurl(HttpClient)} if
     * you want to create {@link CVurl} with given HttpClient.
     *
     * @param genericMapper  mapper with which CVurl will be created.
     * @param httpClient     httpClient with which CVurl will be created.
     * @param requestTimeout requestTimeout with which CVurl will be created.
     */
    @Deprecated
    public CVurl(GenericMapper genericMapper, HttpClient httpClient, Duration requestTimeout) {
        this.configuration = Configuration.builder(httpClient)
                .genericMapper(genericMapper)
                .requestTimeout(requestTimeout)
                .build();
    }

    public Configuration getConfiguration() {
        return configuration;
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

    private RequestBuilder<?> createGetRequest(String url) {
        return new RequestBuilder<>(url, HttpMethod.GET, configuration);
    }

    private RequestWithBodyBuilder createRequestWBody(String url, HttpMethod httpMethod) {
        return new RequestWithBodyBuilder(url, httpMethod, configuration);
    }
}
