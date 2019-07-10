package coresearch.cvurl.io.request;

import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.util.HttpClientMode;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CVurlTest {

    @Test
    public void createCurlWithPrototypeHttpClientTest() {
        //given
        var configuration = Configuration.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();

        //when
        var cvurl1 = new CVurl(configuration);
        var cvurl2 = new CVurl(configuration);

        //then
        assertNotSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCurlWithSingletoneHttpClientTest() {
        //given
        var configuration = Configuration.builder().httpClientMode(HttpClientMode.SINGLETONE).build();

        //when
        var cvurl1 = new CVurl(configuration);
        var cvurl2 = new CVurl(configuration);

        //then
        assertSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    private HttpClient getHttpClient(CVurl cvurl) {
        try {
            var httpClient = CVurl.class.getDeclaredField("httpClient");
            httpClient.setAccessible(true);

            return (HttpClient) httpClient.get(cvurl);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
