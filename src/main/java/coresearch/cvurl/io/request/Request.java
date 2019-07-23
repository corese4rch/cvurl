package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.model.Response;
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

    Request(HttpRequest httpRequest, HttpClient httpClient, GenericMapper genericMapper) {
        this.httpRequest = httpRequest;
        this.httpClient = httpClient;
        this.genericMapper = genericMapper;
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
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    /**
     * Sends current request asynchronously.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public CompletableFuture<Response<String>> asyncAsString() {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply(Response::new);
    }

    /**
     * Sends current request asynchronously. Returns response with body as {@link InputStream}
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofInputStream())
                .thenApply(Response::new);
    }

    /**
     * Sends current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler) {
        return this.httpClient.sendAsync(httpRequest, bodyHandler)
                .thenApply(Response::new);
    }

    /**
     * Sends current request asynchronously and maps response body using
     * provided mapper function.
     *
     * @param bodyMapper function to map response body to some object
     * @param <T>        type of the mapped object
     * @return {@link CompletableFuture} with returned response with mapped body.
     */
    public <T> CompletableFuture<Response<T>> asyncMap(Function<String, T> bodyMapper) {
        return this.httpClient.sendAsync(httpRequest, new ResponseStringMappingBodyHandler<>(bodyMapper))
                .thenApply(Response::new);
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
    public <T> T asObject(Class<T> type, int statusCode) {
        return sendRequestForObject(type, statusCode);
    }

    /**
     * Sends current request blocking if necessary to get
     * the response.
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public Optional<Response<String>> asString() {
        return sendRequestAndHandleExceptions(BodyHandlers.ofString());
    }

    /**
     * Sends current request blocking if necessary to get
     * the response as {@link InputStream}
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public Optional<Response<InputStream>> asStream() {
        return sendRequestAndHandleExceptions(BodyHandlers.ofInputStream());
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
        return sendRequestAndHandleExceptions(bodyHandler);
    }

    /**
     * Sends current request blocking if necessary to get
     * the response. Maps response body using provided mapper function.
     *
     * @param bodyMapper function to map response body to some object
     * @param <T>        type of the mapped object
     * @return {@link Optional} with response with mapped body if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    public <T> Optional<Response<T>> map(Function<String, T> bodyMapper) {
        return sendRequestAndHandleExceptions(new ResponseStringMappingBodyHandler<>(bodyMapper));
    }

    private <T> Optional<Response<T>> sendRequestAndHandleExceptions(HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            HttpResponse<T> response = sendRequest(bodyHandler);
            return Optional.of(new Response<>(response));

        } catch (InterruptedException | IOException e) {
            LOGGER.error("Error while sending request: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private <T> T sendRequestForObject(Class<T> type, int statusCode) {
        try {
            HttpResponse<String> response = sendRequest(BodyHandlers.ofString());
            return parseResponse(response, type, statusCode);

        } catch (InterruptedException | IOException e) {
            throw new RequestExecutionException(e.getMessage(), e);
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

    private <T> HttpResponse<T> sendRequest(HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        LOGGER.info("Sending request {}", this.httpRequest);
        return this.httpClient.send(this.httpRequest, bodyHandler);
    }
}
