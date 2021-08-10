package coresearch.cvurl.io.model;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Wrapper for the {@link HttpResponse} class.
 *
 * @since 0.9
 * @param <T> the response body type
 */
public class Response<T> {

    private final HttpResponse<T> rawResponse;

    /**
     * Creates an instance of the {@link Response} class from the specified {@link HttpResponse} instance.
     *
     * @param rawResponse - the HTTP request response
     */
    public Response(HttpResponse<T> rawResponse) {
        this.rawResponse = rawResponse;
    }

    /**
     * Returns the {@link HttpResponse#statusCode()} value.
     */
    public int status() {
        return rawResponse.statusCode();
    }

    /**
     * Returns the {@link HttpRequest} instance corresponding to this response.
     *
     * <p> The returned {@code HttpRequest} may not be the initiating request
     * provided when {@linkplain HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
     * sending}. For example, if the initiating request was redirected, then the
     * request returned by this method will have the redirected URI, which will
     * be different from the initiating request URI.
     *
     * @see #previousResponse()
     */
    public HttpRequest request() {
        return rawResponse.request();
    }

    /**
     * Returns an {@code Optional} containing the previous intermediate response
     * if one was received. An intermediate response is one that is received
     * as a result of redirection or authentication. If no previous response
     * was received then an empty {@code Optional} is returned.
     */
    public Optional<HttpResponse<T>> previousResponse() {
        return rawResponse.previousResponse();
    }

    /**
     * Returns the {@link HttpResponse#headers()} value.
     */
    public HttpHeaders headers() {
        return rawResponse.headers();
    }


    /**
     * Returns an {@link Optional} containing the {@link SSLSession} in effect
     * for this response. Returns an empty {@code Optional} if this is not a
     * <i>HTTPS</i> response.
     */
    public Optional<SSLSession> sslSession() {
        return rawResponse.sslSession();
    }

    /**
     * Returns the {@code URI} that the response was received from. This may be
     * different from the request {@code URI} if redirection occurred.
     */
    public URI uri() {
        return rawResponse.uri();
    }

    /**
     * Returns the {@link HttpResponse#headers()} value.
     */
    public HttpClient.Version version() {
        return rawResponse.version();
    }

    /**
     * Returns true if {@link Response#status()} is in the range [200..300).
     */
    public boolean isSuccessful() {
        return status() >= 200 && status() < 300;
    }

    /**
     * Returns header names.
     */
    public Set<String> headersNames() {
        return headers().map().keySet();
    }

    /**
     * Returns the value for the specified header name. Returns an empty {@code Optional} if no title with that name exists.
     *
     * @param headerName - the name of the header
     */
    public Optional<String> getHeaderValue(String headerName) {
        List<String> headerValues = headers().map().get(headerName);

        return headerValues == null ? Optional.empty() : Optional.of(String.join(",", headerValues));
    }

    /**
     * Returns the header values as a list.
     *
     * @param headerName - the name of the header
     */
    public List<String> getHeaderValuesAsList(String headerName) {
        return headers().allValues(headerName);
    }

    /**
     * Returns the {@link HttpResponse#body()} value.
     */
    public T getBody() {
        return rawResponse.body();
    }

    /**
     * Returns the {@link HttpResponse#toString()} value.
     */
    @Override
    public String toString() {
        return rawResponse.toString();
    }
}
