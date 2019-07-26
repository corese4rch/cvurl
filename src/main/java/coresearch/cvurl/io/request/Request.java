package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.ResponseBodyHandlingException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.request.handler.CompressedInputStreamBodyHandler;
import coresearch.cvurl.io.request.handler.CompressedStringBodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Class responsible for sending HTTP requests and parsing responses.
 * Can be created by using {@link RequestBuilder#build()}
 */
public final class Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    private HttpClient httpClient;
    private HttpRequest httpRequest;
    private GenericMapper genericMapper;
    private boolean acceptCompressed;

    Request(HttpRequest httpRequest, HttpClient httpClient, GenericMapper genericMapper, boolean acceptCompressed) {
        this.httpRequest = httpRequest;
        this.httpClient = httpClient;
        this.genericMapper = genericMapper;
        this.acceptCompressed = acceptCompressed;
    }

    /**
     * Sends current request asynchronously. If response status code
     * matches provided status code then returns {@link CompletableFuture}
     * with object of provided type. Otherwise returns {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return {@link CompletableFuture} with object of provided type or {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}
     */
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode) {
        return this.httpClient.sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    /**
     * Sends current request asynchronously.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public CompletableFuture<Response<String>> asyncAsString() {
        return this.httpClient.sendAsync(httpRequest, getStringBodyHandler()).thenApply(Response::new);
    }

    /**
     * Sends current request asynchronously. Returns response with body as {@link InputStream}
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return this.httpClient.sendAsync(httpRequest, getStreamBodyHandler()).thenApply(Response::new);
    }

    /**
     * Sends current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler) {
        return this.httpClient.sendAsync(httpRequest, bodyHandler).thenApply(Response::new);
    }

    /**
     * Sends current request blocking if necessary to get
     * the response. Converts response body to specified type if
     * provided statusCode matches response status code and throws
     * {@link UnexpectedResponseException} otherwise. Throws {@link RequestExecutionException}
     * if some error happens on request sending.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return object of specified type
     */
    public <T> Optional<T> asObject(Class<T> type, int statusCode) {
        return sendRequestAndMapResponse(getStringBodyHandler(),
                (response) -> parseResponse(response, type, statusCode));
    }

    /**
     * Sends current request blocking if necessary to get
     * the response.
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public Optional<Response<String>> asString() {
        return sendRequestAndMapResponse(getStringBodyHandler(), Response::new);
    }

    /**
     * Sends current request blocking if necessary to get
     * the response as {@link InputStream}
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public Optional<Response<InputStream>> asStream() {
        return sendRequestAndMapResponse(getStreamBodyHandler(), Response::new);
    }

    /**
     * Sends current request blocking if necessary to get
     * the response with body parsed by provided bodyHandler.
     *
     * @param bodyHandler used to parse response body
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler) {
        return sendRequestAndMapResponse(bodyHandler, Response::new);
    }

    private HttpResponse.BodyHandler<String> getStringBodyHandler() {
        return acceptCompressed ? new CompressedStringBodyHandler() : BodyHandlers.ofString();
    }

    private HttpResponse.BodyHandler<InputStream> getStreamBodyHandler() {
        return acceptCompressed ? new CompressedInputStreamBodyHandler() : BodyHandlers.ofInputStream();
    }

    private <T, U> Optional<T> sendRequestAndMapResponse(HttpResponse.BodyHandler<U> bodyHandler,
                                                         Function<HttpResponse<U>, T> responseMapper) {
        LOGGER.info("Sending request {}", this.httpRequest);
        try {
            HttpResponse<U> response = this.httpClient.send(this.httpRequest, bodyHandler);
            return Optional.of(responseMapper.apply(response));

        } catch (Exception e) {
            LOGGER.error("Error while sending request: {} exception happened with message {}", e.toString(), e.getMessage());
            return Optional.empty();
        }
    }

    private <T> T parseResponse(HttpResponse<String> response, Class<T> type, int statusCode) {
        if (response.statusCode() != statusCode) {
            throw new UnexpectedResponseException("Received response with status code: " + response.statusCode() +
                    ",expected: " + statusCode + ";Response: " + response.body(),
                    new Response<>(response));
        }
        return genericMapper.readValue(response.body(), type);
    }
}
