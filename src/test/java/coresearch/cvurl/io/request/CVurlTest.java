package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.CvurlConfig;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class CVurlTest {

    @Test
    public void createCVurlWithPrototypeHttpClientTest() {
        //given
        var configuration1 = CvurlConfig.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();
        var configuration2 = CvurlConfig.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();

        //when
        var cvurl1 = new CVurl(configuration1);
        var cvurl2 = new CVurl(configuration2);

        //then
        assertNotSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCVurlWithSingletoneHttpClientTest() {
        //given
        var configuration1 = CvurlConfig.builder().httpClientMode(HttpClientMode.SINGLETON).build();
        var configuration2 = CvurlConfig.builder().httpClientMode(HttpClientMode.SINGLETON).build();

        //when
        var cvurl1 = new CVurl(configuration1);
        var cvurl2 = new CVurl(configuration2);

        //then
        assertSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCVurlWithNullConfigShouldThrowNPE() {
        //given
        CvurlConfig cvurlConfig = null;

        //then
        assertThrows(NullPointerException.class, () -> new CVurl(cvurlConfig));
        assertThrows(NullPointerException.class, () -> new CVurl(MapperFactory.createDefault(), cvurlConfig));
    }

    private HttpClient getHttpClient(CVurl cvurl) {
        return cvurl.getCvurlConfig().getHttpClient();
    }
}
