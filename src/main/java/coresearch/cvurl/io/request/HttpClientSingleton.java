package coresearch.cvurl.io.request;

import java.net.http.HttpClient;

import static java.lang.String.format;

public final class HttpClientSingleton {

    private static HttpClient httpClient;

    private HttpClientSingleton() {
        throw new IllegalStateException(format("Creating of class %s is forbidden", HttpClientSingleton.class.getName()));
    }

    public static HttpClient getClient(HttpClient fromClient) {
        HttpClient client = httpClient;
        if (null == client) {
            synchronized (HttpClientSingleton.class) {
                client = httpClient;
                if (null == client) {
                    httpClient = client = fromClient;
                }
            }
        }
        return client;
    }
}
