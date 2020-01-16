package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.Configuration;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class CVurlTest {

    @Test
    public void createCVurlWithPrototypeHttpClientTest() {
        //given
        var configuration1 = Configuration.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();
        var configuration2 = Configuration.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();

        //when
        var cvurl1 = new CVurl(configuration1);
        var cvurl2 = new CVurl(configuration2);

        //then
        assertNotSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCVurlWithSingletoneHttpClientTest() {
        //given
        var configuration1 = Configuration.builder().httpClientMode(HttpClientMode.SINGLETON).build();
        var configuration2 = Configuration.builder().httpClientMode(HttpClientMode.SINGLETON).build();

        //when
        var cvurl1 = new CVurl(configuration1);
        var cvurl2 = new CVurl(configuration2);

        //then
        assertSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCVurlWithNullConfigShouldThrowNPE() {
        //given
        Configuration configuration = null;

        //then
        assertThrows(NullPointerException.class, () -> new CVurl(configuration));
        assertThrows(NullPointerException.class, () -> new CVurl(MapperFactory.createDefault(), configuration));
    }

    private HttpClient getHttpClient(CVurl cvurl) {
        return cvurl.getConfiguration().getHttpClient();
    }
}
