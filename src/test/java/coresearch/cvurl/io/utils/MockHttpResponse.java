package coresearch.cvurl.io.utils;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class MockHttpResponse implements HttpResponse<String> {

    private URI uri;
    private HttpHeaders headers;
    private HttpRequest httpRequest;
    private HttpResponse<String> previousResponse;
    private SSLSession sslSession;
    private String body;
    private HttpClient.Version version;

    private MockHttpResponse() { }

    public static MockHttpResponse create() {
        return new MockHttpResponse();
    }

    @Override
    public int statusCode() {
        return 0;
    }

    @Override
    public HttpRequest request() {
        return httpRequest;
    }

    @Override
    public Optional<HttpResponse<String>> previousResponse() {
        return Optional.ofNullable(previousResponse);
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return Optional.ofNullable(sslSession);
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public HttpClient.Version version() {
        return version;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setPreviousResponse(HttpResponse<String> previousResponse) {
        this.previousResponse = previousResponse;
    }

    public void setSslSession(SSLSession sslSession) {
        this.sslSession = sslSession;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setVersion(HttpClient.Version version) {
        this.version = version;
    }
}
