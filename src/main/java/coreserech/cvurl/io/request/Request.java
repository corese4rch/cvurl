package coreserech.cvurl.io.request;

import coreserech.cvurl.io.exception.RequestExecutionException;
import coreserech.cvurl.io.exception.UnexpectedResponseException;
import coreserech.cvurl.io.mapper.GenericMapper;
import coreserech.cvurl.io.model.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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

    public <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode) {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply(response -> sendRequestForObject(type, statusCode));
    }

    public CompletableFuture<Response<String>> asyncAsString() {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply(response -> new Response<>(response.body(), response));
    }

    public CompletableFuture<Response<JSONObject>> asyncAsJsonObject() {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply(response -> new Response<>(new JSONObject(response.body()), response));
    }

    public CompletableFuture<Response<JSONArray>> asyncAsJsonArray() {
        return this.httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
                .thenApply(response -> new Response<>(new JSONArray(response.body()), response));
    }

    public Optional<Response<JSONObject>> asJsonObject() {
        return sendRequestAndConvertResponseBody(JSONObject::new);
    }

    public Optional<Response<JSONArray>> asJsonArray() {
        return sendRequestAndConvertResponseBody(JSONArray::new);
    }

    public <T> T asObject(Class<T> type, int statusCode) {
        return sendRequestForObject(type, statusCode);
    }

    public Optional<Response<String>> asString() {
        return sendRequestAndConvertResponseBody(Function.identity());
    }

    private <T> Optional<Response<T>> sendRequestAndConvertResponseBody(Function<String, T> responseBodyConverter) {
        try {
            HttpResponse<String> response = sendRequest();
            return Optional.of(new Response<>(responseBodyConverter.apply(response.body()), response));

        } catch (InterruptedException | IOException e) {
            LOGGER.error("Error while sending request: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private <T> T sendRequestForObject(Class<T> type, int statusCode) {
        try {
            HttpResponse<String> response = sendRequest();
            if (response.statusCode() != statusCode) {
                throw new UnexpectedResponseException("Received response with status code: " + response.statusCode() +
                        ",expected: " + statusCode + ";Response: " + response.body(),
                        new Response<>(response.body(), response));
            }
            return genericMapper.readValue(response.body(), type);

        } catch (InterruptedException | IOException e) {
            LOGGER.error("Error while sending request: {}", e.getMessage());
            throw new RequestExecutionException(e.getMessage(), e);
        }
    }

    private HttpResponse<String> sendRequest() throws IOException, InterruptedException {
        LOGGER.info("Sending request {}", this.httpRequest);
        return this.httpClient.send(this.httpRequest, BodyHandlers.ofString());
    }
}
