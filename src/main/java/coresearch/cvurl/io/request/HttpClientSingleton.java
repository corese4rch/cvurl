package coresearch.cvurl.io.request;

import java.net.http.HttpClient;

public class HttpClientSingleton {

    private static volatile HttpClient httpClient;

    @SuppressWarnings({"UnusedAssignment", "ConstantConditions"})
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
