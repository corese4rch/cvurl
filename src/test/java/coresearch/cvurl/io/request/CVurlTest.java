package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.constant.HttpClientMode;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class CVurlTest {

    @Test
    public void createCurlWithPrototypeHttpClientTest() {
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
    public void createCurlWithSingletoneHttpClientTest() {
        //given
        var configuration1 = Configuration.builder().httpClientMode(HttpClientMode.SINGLETONE).build();
        var configuration2 = Configuration.builder().httpClientMode(HttpClientMode.SINGLETONE).build();

        //when
        var cvurl1 = new CVurl(configuration1);
        var cvurl2 = new CVurl(configuration2);

        //then
        assertSame(getHttpClient(cvurl1), getHttpClient(cvurl2));
    }

    @Test
    public void createCurlWithNullConfigShouldThrowNPE() {
        //given
        Configuration configuration = null;

        //then
        assertThrows(NullPointerException.class, () -> new CVurl(configuration));
        assertThrows(NullPointerException.class, () -> new CVurl(MapperFactory.createDefault(), configuration));
    }

    private Configuration getConfiguration(CVurl cvurl) {
        try {
            var configurationField = CVurl.class.getDeclaredField("configuration");
            configurationField.setAccessible(true);

            return (Configuration) configurationField.get(cvurl);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private HttpClient getHttpClient(CVurl cvurl) {
        return getConfiguration(cvurl).getHttpClient();
    }
}
