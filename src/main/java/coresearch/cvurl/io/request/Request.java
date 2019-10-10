package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.CVType;
import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Request {
    <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode);

    <T> CompletableFuture<T> asyncAsObject(CVType<T> type, int statusCode);

    <T> CompletableFuture<T> asyncAsObject(Class<T> type);

    <T> CompletableFuture<T> asyncAsObject(CVType<T> type);

    CompletableFuture<Response<String>> asyncAsString();

    CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph);

    CompletableFuture<Response<InputStream>> asyncAsStream();

    CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph);

    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler);

    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph);

    <T> Optional<T> asObject(Class<T> type, int statusCode);

    <T> Optional<T> asObject(CVType<T> type, int statusCode);

    <T> T asObject(Class<T> type);

    <T> T asObject(CVType<T> type);

    Optional<Response<String>> asString();

    Optional<Response<InputStream>> asStream();

    <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler);
}
