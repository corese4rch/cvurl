package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.model.CVurlConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class CVurlTest {

    @Test
    void shouldReturnNewHttpClientForEachCVurlConfigWhenHttpClientModeIsPrototype() {
        //given
        var configuration1 = CVurlConfig.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();
        var configuration2 = CVurlConfig.builder().httpClientMode(HttpClientMode.PROTOTYPE).build();

        //when
        var cVurl1 = new CVurl(configuration1);
        var cVurl2 = new CVurl(configuration2);

        //then
        assertNotSame(getHttpClient(cVurl1), getHttpClient(cVurl2));
    }

    @Test
    void shouldReturnSameHttpClientForEachCVurlConfigWhenHttpClientModeIsSingleton() {
        //given
        var configuration1 = CVurlConfig.builder().httpClientMode(HttpClientMode.SINGLETON).build();
        var configuration2 = CVurlConfig.builder().httpClientMode(HttpClientMode.SINGLETON).build();

        //when
        var cVurl1 = new CVurl(configuration1);
        var cVurl2 = new CVurl(configuration2);

        //then
        assertSame(getHttpClient(cVurl1), getHttpClient(cVurl2));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCVurlConfigIsNull() {
        //given
        CVurlConfig cVurlConfig = null;

        //when
        Executable executable = () -> new CVurl(cVurlConfig);

        //then
        assertThrows(NullPointerException.class, executable);
    }

    private HttpClient getHttpClient(CVurl cVurl) {
        return cVurl.getCvurlConfig().getHttpClient();
    }
}
