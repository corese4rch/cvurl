package coresearch.cvurl.io.request;

import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Request {
    <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode);

    <T> CompletableFuture<T> asyncAsObject(Class<T> type);

    CompletableFuture<Response<String>> asyncAsString();

    CompletableFuture<Response<InputStream>> asyncAsStream();

    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler);

    <T> Optional<T> asObject(Class<T> type, int statusCode);

    <T> T asObject(Class<T> type);

    Optional<Response<String>> asString();

    Optional<Response<InputStream>> asStream();

    <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler);
}
