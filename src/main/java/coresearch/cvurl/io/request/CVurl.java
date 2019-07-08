package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.util.HttpClientMode;
import coresearch.cvurl.io.util.HttpMethod;

import java.net.URL;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Central part of the lib. Used to initiate {@link Request} creation with proper {@link GenericMapper} and
 * {@link HttpClient}.
 */
public class CVurl {

    private final GenericMapper genericMapper;

    private final HttpClient httpClient;

    private final Duration requestTimeout;

    /**
     * Creates CVurl with default mapper created by {@link MapperFactory#createDefault()}, default {@link HttpClient} created
     * by {@link HttpClient#newHttpClient()} and request timeout set to null.
     */
    public CVurl() {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = HttpClient.newHttpClient();
        this.requestTimeout = null;
    }

    /**
     * Creates CVurl with specified {@link GenericMapper}, default {@link HttpClient} created
     * by {@link HttpClient#newHttpClient()} and request timeout set to null.
     *
     * @param genericMapper mapper with which CVurl will be created.
     */
    public CVurl(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.requestTimeout = null;
    }

    /**
     * Creates CVurl with specified {@link Configuration} and
     * default mapper created by {@link MapperFactory#createDefault()}.
     *
     * @param configuration configuration with which CVurl will be created.
     */
    public CVurl(Configuration configuration) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = getHttpClient(configuration);
        this.requestTimeout = configuration.getRequestTimeout();
    }

    /**
     * Creates CVurl with specified {@link GenericMapper} and {@link Configuration}
     *
     * @param genericMapper mapper with which CVurl will be created.
     * @param configuration configuration with which CVurl will be created.
     */
    public CVurl(GenericMapper genericMapper, Configuration configuration) {
        this.genericMapper = genericMapper;
        this.httpClient = getHttpClient(configuration);
        this.requestTimeout = configuration.getRequestTimeout();
    }

    /**
     * Creates CVurl with specified {@link HttpClient} using
     * default mapper created by {@link MapperFactory#createDefault()} and
     * request timeout set to null.
     *
     * @param httpClient httpClient with which CVurl will be created.
     */
    public CVurl(HttpClient httpClient) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = httpClient;
        this.requestTimeout = null;
    }

    /**
     * Creates CVurl with specified {@link HttpClient} and request timeout using
     * default mapper created by {@link MapperFactory#createDefault()}.
     *
     * @param httpClient     httpClient with which CVurl will be created.
     * @param requestTimeout requestTimeout with which CVurl will be created.
     */
    public CVurl(HttpClient httpClient, Duration requestTimeout) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = httpClient;
        this.requestTimeout = requestTimeout;
    }

    /**
     * Creates CVurl with specified {@link HttpClient} and {@link GenericMapper}.
     * Timeout duration is set to null.
     *
     * @param genericMapper mapper with which CVurl will be created.
     * @param httpClient    httpClient with which CVurl will be created.
     */
    public CVurl(GenericMapper genericMapper, HttpClient httpClient) {
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
        this.requestTimeout = null;
    }

    /**
     * Creates CVurl with specified {@link HttpClient}, {@link GenericMapper} and
     * timeout duration.
     *
     * @param genericMapper  mapper with which CVurl will be created.
     * @param httpClient     httpClient with which CVurl will be created.
     * @param requestTimeout requestTimeout with which CVurl will be created.
     */
    public CVurl(GenericMapper genericMapper, HttpClient httpClient, Duration requestTimeout) {
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
        this.requestTimeout = requestTimeout;
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.GET.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestBuilder GET(String url) {
        return createRequest(url, HttpMethod.GET);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.GET.
     *
     * @param url specified url.
     * @return RequestBuilder
     */

    public RequestBuilder GET(URL url) {
        return createRequest(url.toString(), HttpMethod.GET);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.POST.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder POST(String url) {
        return createRequestWBody(url, HttpMethod.POST);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.POST.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder POST(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.POST);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PUT.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder PUT(String url) {
        return createRequestWBody(url, HttpMethod.PUT);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PUT.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder PUT(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PUT);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.DELETE.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder DELETE(String url) {
        return createRequestWBody(url, HttpMethod.DELETE);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.DELETE.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder DELETE(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.DELETE);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PATCH.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder PATCH(String url) {
        return createRequestWBody(url, HttpMethod.PATCH);
    }

    /**
     * Creates RequestBuilder with specified url and HttpMethod.PATCH.
     *
     * @param url specified url.
     * @return RequestBuilder
     */
    public RequestWithBodyBuilder PATCH(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PATCH);
    }

    private HttpClient getHttpClient(Configuration configuration) {
        return configuration.getHttpClientMode() == HttpClientMode.PROTOTYPE ?
                configuration.createHttpClient() : HttpClientSingleton.getClient(configuration);
    }

    private RequestBuilder createRequest(String url, HttpMethod httpMethod) {
        return new RequestBuilder(url, httpMethod, this.genericMapper, this.httpClient).timeout(requestTimeout);
    }

    private RequestWithBodyBuilder createRequestWBody(String url, HttpMethod httpMethod) {
        return new RequestWithBodyBuilder(url, httpMethod, this.genericMapper, this.httpClient).timeout(requestTimeout);
    }

    private static class HttpClientSingleton {

        private static final Lock lock = new ReentrantLock();
        private static volatile HttpClient httpClient;

        static HttpClient getClient(Configuration configuration) {
            HttpClient client = httpClient;
            if (null == client) {
                lock.lock();
                try {
                    client = httpClient;
                    if (null == client) {
                        if (configuration != null) {
                            client = configuration.createHttpClient();
                        } else {
                            client = HttpClient.newHttpClient();
                        }
                        httpClient = client;
                    }
                } finally {
                    lock.unlock();
                }
            }
            return client;
        }
    }
}