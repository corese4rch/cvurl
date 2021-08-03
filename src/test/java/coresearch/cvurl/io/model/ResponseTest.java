package coresearch.cvurl.io.model;

import coresearch.cvurl.io.utils.MockHttpRequest;
import coresearch.cvurl.io.utils.MockHttpResponse;
import coresearch.cvurl.io.utils.MockSSLSession;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ResponseTest {

    private final MockHttpResponse httpResponse = MockHttpResponse.create();

    @Test
    void shouldReturnProvidedRequest() {
        //given
        var request = MockHttpRequest.create();
        httpResponse.setHttpRequest(request);

        //when
        var result = new Response<>(httpResponse).request();

        //then
        assertSame(request, result);
    }

    @Test
    void shouldReturnProvidedPreviousResponse() {
        //given
        var previousResponse = MockHttpResponse.create();
        httpResponse.setPreviousResponse(previousResponse);

        //when
        var result = new Response<>(httpResponse).previousResponse().orElseThrow(RuntimeException::new);

        //then
        assertSame(previousResponse, result);
    }

    @Test
    void shouldReturnProvidedSslSession() {
        //given
        var sslSession = MockSSLSession.create();
        httpResponse.setSslSession(sslSession);

        //when
        var result = new Response<>(httpResponse).sslSession().orElseThrow(RuntimeException::new);

        //then
        assertSame(sslSession, result);
    }

    @Test
    void shouldReturnProvidedUri() throws URISyntaxException {
        //given
        var uri = new URI("//https://www.google.com/");
        httpResponse.setUri(uri);

        //when
        var result = new Response<>(httpResponse).uri();

        //then
        assertSame(uri, result);
    }

    @Test
    void shouldReturnProvidedVersion() {
        //given
        var version = HttpClient.Version.HTTP_1_1;
        httpResponse.setVersion(version);

        //when
        var result = new Response<>(httpResponse).version();

        //then
        assertSame(version, result);
    }

    @Test
    void shouldReturnProvidedHeaders() {
        //given
        var key = "key";
        var values = List.of("val1", "val2");
        var headers = HttpHeaders.of(Map.of(key, values), (k, v) -> true);
        httpResponse.setHeaders(headers);

        //when
        var result = new Response<>(httpResponse).getHeaderValuesAsList(key);

        //then
        assertIterableEquals(values, result);
    }
}