package coreserech.cvurl.io.model;

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
 * Wrapper around Java 11 {@link HttpResponse}.
 *
 * @param <T>
 */
public class Response<T> {

    private T body;

    private HttpResponse<String> rawResponse;

    /**
     * Creates new Response from specified body and raw {@link HttpResponse}.
     *
     * @param body        parsed response body
     * @param rawResponse response
     */
    public Response(T body, HttpResponse<String> rawResponse) {
        this.body = body;
        this.rawResponse = rawResponse;
    }

    /**
     * Returns the status code for this response.
     *
     * @return the response code
     */
    public int status() {
        return rawResponse.statusCode();
    }

    /**
     * Returns the {@link HttpRequest} corresponding to this response.
     *
     * <p> The returned {@code HttpRequest} may not be the initiating request
     * provided when {@linkplain HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
     * sending}. For example, if the initiating request was redirected, then the
     * request returned by this method will have the redirected URI, which will
     * be different from the initiating request URI.
     *
     * @return the request
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
     *
     * @return an Optional containing the HttpResponse, if any.
     */
    public Optional<HttpResponse<String>> previousResponse() {
        return rawResponse.previousResponse();
    }

    /**
     * Returns the received response headers.
     *
     * @return the response headers
     */
    public HttpHeaders headers() {
        return rawResponse.headers();
    }


    /**
     * Returns an {@link Optional} containing the {@link SSLSession} in effect
     * for this response. Returns an empty {@code Optional} if this is not a
     * <i>HTTPS</i> response.
     *
     * @return an {@code Optional} containing the {@code SSLSession} associated
     * with the response
     */
    public Optional<SSLSession> sslSession() {
        return rawResponse.sslSession();
    }

    /**
     * Returns the {@code URI} that the response was received from. This may be
     * different from the request {@code URI} if redirection occurred.
     *
     * @return the URI of the response
     */
    public URI uri() {
        return rawResponse.uri();
    }

    /**
     * Returns the HTTP protocol version that was used for this response.
     *
     * @return HTTP protocol version
     */
    public HttpClient.Version version() {
        return rawResponse.version();
    }

    /**
     * Checks if response is successful (status code is 2XX)
     *
     * @return whether response is successful.
     */
    public boolean isSuccessful() {
        return status() >= 200 && status() < 300;
    }

    /**
     * Returns headers names.
     *
     * @return headers names
     */
    public Set<String> headersNames() {
        return headers().map().keySet();
    }

    /**
     * Returns value for specified header name.
     *
     * @param headerName
     * @return optional of header value. Is empty if no header with such name exists.
     */
    public Optional<String> getHeaderValue(String headerName) {
        List<String> headerValues = headers().map().get(headerName);

        return headerValues == null ? Optional.empty() : Optional.of(String.join(",", headerValues));
    }

    /**
     * Return header values as list.
     *
     * @param headerName
     * @return header values as list.
     */
    public List<String> getHeaderValuesAsList(String headerName) {
        return headers().allValues(headerName);
    }

    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        return rawResponse.toString();
    }
}
