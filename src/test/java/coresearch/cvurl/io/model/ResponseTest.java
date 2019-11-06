package coresearch.cvurl.io.model;

import coresearch.cvurl.io.utils.MockHttpRequest;
import coresearch.cvurl.io.utils.MockHttpResponse;
import coresearch.cvurl.io.utils.MockSSLSession;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ResponseTest {

    public MockHttpResponse httpResponse = MockHttpResponse.create();

    @Test
    public void requestTest() {
        //given
        HttpRequest request = MockHttpRequest.create();
        httpResponse.setHttpRequest(request);

        //when
        HttpRequest result = new Response<>(httpResponse).request();

        //then
        assertSame(request, result);
    }

    @Test
    public void previousResponseTest() {
        //given
        HttpResponse<String> previousResponse = MockHttpResponse.create();
        httpResponse.setPreviousResponse(previousResponse);

        //when
        HttpResponse<String> result = new Response<>(httpResponse).previousResponse().orElseThrow(RuntimeException::new);

        //then
        assertSame(previousResponse, result);
    }

    @Test
    public void sslSessionTest() {
        //given
        SSLSession sslSession = MockSSLSession.create();
        httpResponse.setSslSession(sslSession);

        //when
        SSLSession result = new Response<>(httpResponse).sslSession().orElseThrow(RuntimeException::new);

        //then
        assertSame(sslSession, result);
    }

    @Test
    public void uriTest() throws URISyntaxException {
        //given
        URI uri = new URI("//https://www.google.com/");
        httpResponse.setUri(uri);

        //when
        URI result = new Response<>(httpResponse).uri();

        //then
        assertSame(uri, result);
    }

    @Test
    public void versionTest() {
        //given
        HttpClient.Version version = HttpClient.Version.HTTP_1_1;
        httpResponse.setVersion(version);
        //when
        HttpClient.Version result = new Response<>(httpResponse).version();

        //then
        assertSame(version, result);
    }

    @Test
    public void getHeaderValuesAsListTest() {
        //given
        String key = "key";
        List<String> values = List.of("val1", "val2");
        HttpHeaders headers = HttpHeaders.of(Map.of(key, values), (k, v) -> true);
        httpResponse.setHeaders(headers);

        //when
        List<String> result = new Response<>(httpResponse).getHeaderValuesAsList(key);

        //then
        assertIterableEquals(values, result);
    }
}