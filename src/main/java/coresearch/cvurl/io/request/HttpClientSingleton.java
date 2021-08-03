package coresearch.cvurl.io.request;

import java.net.http.HttpClient;

import static java.lang.String.format;

/**
 * The utility class for getting the singleton instance of the {@link HttpClient} class.
 *
 * @since 1.2
 */
public final class HttpClientSingleton {

    private static HttpClient httpClient;

    private HttpClientSingleton() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", HttpClientSingleton.class.getName()));
    }

    /**
     * Returns the singleton instance of the {@link HttpClient} class.
     *
     * @param fromClient - the instance of the {@link HttpClient} class
     * @return the singleton instance of the {@link HttpClient} class
     */
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
