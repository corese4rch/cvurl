package coresearch.cvurl.io.model;

import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResponseTest {

    @Mock
    public HttpResponse<String> httpResponse;

    @Test
    public void requestTest() {
        //given
        HttpRequest request = mock(HttpRequest.class);
        when(httpResponse.request()).thenReturn(request);

        //when
        HttpRequest result = new Response<>(httpResponse).request();

        //then
        assertSame(request, result);
    }

    @Test
    public void previousResponseTest() {
        //given
        HttpResponse<String> previousResponse = mock(HttpResponse.class);
        when(httpResponse.previousResponse()).thenReturn(Optional.of(previousResponse));

        //when
        HttpResponse<String> result = new Response<>(httpResponse).previousResponse().orElseThrow(RuntimeException::new);

        //then
        assertSame(previousResponse, result);
    }

    @Test
    public void sslSessionTest() {
        //given
        SSLSession sslSession = mock(SSLSession.class);
        when(httpResponse.sslSession()).thenReturn(Optional.of(sslSession));

        //when
        SSLSession result = new Response<>(httpResponse).sslSession().orElseThrow(RuntimeException::new);

        //then
        assertSame(sslSession, result);
    }

    @Test
    public void uriTest() {
        //given
        URI uri = mock(URI.class);
        when(httpResponse.uri()).thenReturn(uri);

        //when
        URI result = new Response<>(httpResponse).uri();

        //then
        assertSame(uri, result);
    }

    @Test
    public void versionTest() {
        //given
        HttpClient.Version version = mock(HttpClient.Version.class);
        when(httpResponse.version()).thenReturn(version);

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
        when(httpResponse.headers()).thenReturn(headers);

        //when
        List<String> result = new Response<>(httpResponse).getHeaderValuesAsList(key);

        //then
        assertIterableEquals(values, result);
    }
}