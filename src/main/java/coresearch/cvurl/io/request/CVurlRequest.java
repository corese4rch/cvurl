package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
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
 * The class is responsible for sending HTTP requests and parsing HTTP responses.
 * Can be created by using the {@link RequestBuilder#create()} method.
 *
 * @since 0.9
 */
public final class CVurlRequest implements Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(CVurlRequest.class);

    private final CVurlConfig cvurlConfig;
    private final RequestConfiguration requestConfiguration;
    private final HttpClient httpClient;

    private HttpRequest httpRequest;

    CVurlRequest(HttpRequest httpRequest, CVurlConfig cvurlConfig,
                 RequestConfiguration requestConfiguration) {
        this.httpRequest = httpRequest;
        this.cvurlConfig = cvurlConfig;
        this.requestConfiguration = requestConfiguration;
        this.httpClient = cvurlConfig.getHttpClient();
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode) {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type, int statusCode) {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type) {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> cvurlConfig.getGenericMapper().readResponseBody(new Response<>(response), type)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type) {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> cvurlConfig.getGenericMapper().readResponseBody(new Response<>(response), type)));
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString() {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler()).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph) {
        return httpClient.sendAsync(httpRequest, getStringBodyHandler(), pph).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return httpClient.sendAsync(httpRequest, getStreamBodyHandler()).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph) {
        return httpClient.sendAsync(httpRequest, getStreamBodyHandler(), pph).thenApply(Response::new);
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler) {
        return httpClient.sendAsync(httpRequest, bodyHandler).thenApply(Response::new);
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph) {
        return httpClient.sendAsync(httpRequest, bodyHandler, pph).thenApply(Response::new);
    }

    @Override
    public <T> Optional<T> asObject(Class<T> type, int statusCode) {
        return sendRequestAndWrapInOptional(getStringBodyHandler(),
                response -> parseResponse(response, type, statusCode));
    }

    @Override
    public <T> Optional<T> asObject(BodyType<T> type, int statusCode) {
        return sendRequestAndWrapInOptional(getStringBodyHandler(),
                response -> parseResponse(response, type, statusCode));
    }

    @Override
    public <T> T asObject(Class<T> type) {
        return asObject(response -> cvurlConfig.getGenericMapper().readResponseBody(new Response<>(response), type));
    }

    @Override
    public <T> T asObject(BodyType<T> type) {
        return asObject(response -> cvurlConfig.getGenericMapper().readResponseBody(new Response<>(response), type));
    }

    @Override
    public Optional<Response<String>> asString() {
        return sendRequestAndWrapInOptional(getStringBodyHandler(), Response::new);
    }

    @Override
    public Optional<Response<InputStream>> asStream() {
        return sendRequestAndWrapInOptional(getStreamBodyHandler(), Response::new);
    }

    @Override
    public <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler) {
        return sendRequestAndWrapInOptional(bodyHandler, Response::new);
    }

    private HttpResponse.BodyHandler<String> getStringBodyHandler() {
        return requestConfiguration.isAcceptCompressed() ? new CompressedStringBodyHandler() : BodyHandlers.ofString();
    }

    private HttpResponse.BodyHandler<InputStream> getStreamBodyHandler() {
        return requestConfiguration.isAcceptCompressed() ? new CompressedInputStreamBodyHandler() : BodyHandlers.ofInputStream();
    }

    private <T> T asObject(Function<HttpResponse<String>, T> responseMapper) {
        try {
            return sendRequest(getStringBodyHandler(), responseMapper);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RequestExecutionException(ie.getMessage(), ie);
        } catch (IOException io) {
            throw new RequestExecutionException(io.getMessage(), io);
        }
    }

    private <T, U> Optional<T> sendRequestAndWrapInOptional(HttpResponse.BodyHandler<U> bodyHandler,
                                                            Function<HttpResponse<U>, T> responseMapper) {
        try {
            return Optional.of(sendRequest(bodyHandler, responseMapper));
        } catch (InterruptedException ie) {
            LOGGER.error("Error while sending request. Thread execution was interrupted.");
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Error while sending request: {} exception happened with message {}", e, e.getMessage());
            return Optional.empty();
        }
    }

    private <T> T parseResponse(HttpResponse<String> response, Class<T> type, int statusCode) {
        checkIfStatusCodesAreEqual(response, statusCode);
        return cvurlConfig.getGenericMapper().readValue(response.body(), type);
    }

    private <T> T parseResponse(HttpResponse<String> response, BodyType<T> type, int statusCode) {
        checkIfStatusCodesAreEqual(response, statusCode);
        return cvurlConfig.getGenericMapper().readValue(response.body(), type);
    }

    private void checkIfStatusCodesAreEqual(HttpResponse<String> response, int statusCode) {
        if (response.statusCode() != statusCode) {
            throw new UnexpectedResponseException("Received response with status code: " + response.statusCode() +
                    ",expected: " + statusCode + ";Response: " + response.body(),
                    new Response<>(response));
        }
    }

    private <T, U> T sendRequest(HttpResponse.BodyHandler<U> bodyHandler,
                                 Function<HttpResponse<U>, T> responseMapper) throws IOException, InterruptedException {
        if (requestConfiguration.isLogEnabled()) {
            LOGGER.info("Sending request {}", this.httpRequest);
        }
        HttpResponse<U> response = httpClient.send(this.httpRequest, bodyHandler);
        return responseMapper.apply(response);
    }
}
