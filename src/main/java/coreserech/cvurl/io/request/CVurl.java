package coreserech.cvurl.io.request;

import coreserech.cvurl.io.mapper.GenericMapper;
import coreserech.cvurl.io.mapper.MapperFactory;
import coreserech.cvurl.io.model.Configuration;
import coreserech.cvurl.io.util.HttpClientMode;
import coreserech.cvurl.io.util.HttpMethod;

import java.net.URL;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CVurl {

    private final GenericMapper genericMapper;

    private final HttpClient httpClient;

    private final Duration requestTimeout;

    public CVurl() {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = HttpClient.newHttpClient();
        this.requestTimeout = null;
    }

    public CVurl(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.requestTimeout = null;
    }

    public CVurl(Configuration configuration) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = getHttpClient(configuration);
        this.requestTimeout = configuration.getRequestTimeout();
    }

    public CVurl(GenericMapper genericMapper, Configuration configuration) {
        this.genericMapper = genericMapper;
        this.httpClient = getHttpClient(configuration);
        this.requestTimeout = configuration.getRequestTimeout();
    }

    public CVurl(HttpClient httpClient) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = httpClient;
        this.requestTimeout = null;
    }

    public CVurl(HttpClient httpClient, Duration requestTimeout) {
        this.genericMapper = MapperFactory.createDefault();
        this.httpClient = httpClient;
        this.requestTimeout = requestTimeout;
    }

    public CVurl(GenericMapper genericMapper, HttpClient httpClient) {
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
        this.requestTimeout = null;
    }

    public CVurl(GenericMapper genericMapper, HttpClient httpClient, Duration requestTimeout) {
        this.genericMapper = genericMapper;
        this.httpClient = httpClient;
        this.requestTimeout = requestTimeout;
    }

    public RequestBuilder GET(String url) {
        return createRequest(url, HttpMethod.GET);
    }

    public RequestBuilder GET(URL url) {
        return createRequest(url.toString(), HttpMethod.GET);
    }

    public RequestWithBodyBuilder POST(String url) {
        return createRequestWBody(url, HttpMethod.POST);
    }

    public RequestWithBodyBuilder POST(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.POST);
    }

    public RequestWithBodyBuilder PUT(String url) {
        return createRequestWBody(url, HttpMethod.PUT);
    }

    public RequestWithBodyBuilder PUT(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PUT);
    }

    public RequestWithBodyBuilder DELETE(String url) {
        return createRequestWBody(url, HttpMethod.DELETE);
    }

    public RequestWithBodyBuilder DELETE(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.DELETE);
    }

    public RequestWithBodyBuilder PATCH(String url) {
        return createRequestWBody(url, HttpMethod.PATCH);
    }

    public RequestWithBodyBuilder PATCH(URL url) {
        return createRequestWBody(url.toString(), HttpMethod.PATCH);
    }

    public HttpClient getHttpClient() {
        return httpClient;
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
