package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.model.RequestConfiguration;
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
 * Can be created by using {@link RequestBuilder#create()}
 */
public final class CVurlRequest implements Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(CVurlRequest.class);

    private final Configuration configuration;
    private final RequestConfiguration requestConfiguration;

    private HttpRequest httpRequest;

    CVurlRequest(HttpRequest httpRequest, Configuration configuration,
                 RequestConfiguration requestConfiguration) {
        this.httpRequest = httpRequest;
        this.configuration = configuration;
        this.requestConfiguration = requestConfiguration;
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode) {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type, int statusCode) {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> parseResponse(response, type, statusCode)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type) {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> configuration.getGenericMapper().readResponseBody(new Response<>(response), type)));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type) {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler())
                .thenApply((response -> configuration.getGenericMapper().readResponseBody(new Response<>(response), type)));
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString() {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler()).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph) {
        return getHttpClient().sendAsync(httpRequest, getStringBodyHandler(), pph).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return getHttpClient().sendAsync(httpRequest, getStreamBodyHandler()).thenApply(Response::new);
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph) {
        return getHttpClient().sendAsync(httpRequest, getStreamBodyHandler(), pph).thenApply(Response::new);
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler) {
        return getHttpClient().sendAsync(httpRequest, bodyHandler).thenApply(Response::new);
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph) {
        return getHttpClient().sendAsync(httpRequest, bodyHandler, pph).thenApply(Response::new);
    }

    @Override
    public <T> Optional<T> asObject(Class<T> type, int statusCode) {
        return sendRequestAndWrapInOptional(getStringBodyHandler(),
                (response) -> parseResponse(response, type, statusCode));
    }

    @Override
    public <T> Optional<T> asObject(BodyType<T> type, int statusCode) {
        return sendRequestAndWrapInOptional(getStringBodyHandler(),
                (response) -> parseResponse(response, type, statusCode));
    }

    @Override
    public <T> T asObject(Class<T> type) {
        try {
            return sendRequest(getStringBodyHandler(),
                    response -> configuration.getGenericMapper().readResponseBody(new Response<>(response), type));
        } catch (IOException | InterruptedException e) {
            throw new RequestExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T asObject(BodyType<T> type) {
        try {
            return sendRequest(getStringBodyHandler(),
                    response -> configuration.getGenericMapper().readResponseBody(new Response<>(response), type));
        } catch (IOException | InterruptedException e) {
            throw new RequestExecutionException(e.getMessage(), e);
        }
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

    private <T, U> Optional<T> sendRequestAndWrapInOptional(HttpResponse.BodyHandler<U> bodyHandler,
                                                            Function<HttpResponse<U>, T> responseMapper) {
        try {
            return Optional.of(sendRequest(bodyHandler, responseMapper));
        } catch (Exception e) {
            LOGGER.error("Error while sending request: {} exception happened with message {}", e.toString(), e.getMessage());
            return Optional.empty();
        }
    }

    private <T> T parseResponse(HttpResponse<String> response, Class<T> type, int statusCode) {
        checkIfStatusCodesAreEqual(response, statusCode);
        return configuration.getGenericMapper().readValue(response.body(), type);
    }

    private <T> T parseResponse(HttpResponse<String> response, BodyType<T> type, int statusCode) {
        checkIfStatusCodesAreEqual(response, statusCode);
        return configuration.getGenericMapper().readValue(response.body(), type);
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
        LOGGER.info("Sending request {}", this.httpRequest);
        HttpResponse<U> response = getHttpClient().send(this.httpRequest, bodyHandler);
        return responseMapper.apply(response);
    }

    private HttpClient getHttpClient() {
        return configuration.getHttpClient();
    }
}
