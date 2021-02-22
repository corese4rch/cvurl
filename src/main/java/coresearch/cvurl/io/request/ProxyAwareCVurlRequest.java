package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ProxyAwareCVurlRequest implements Request {

    private final CVurlRequest request;
    private final CVurlConfig cVurlConfig;
    private final URI uri;

    public ProxyAwareCVurlRequest(CVurlRequest request, CVurlConfig cVurlConfig, URI uri) {
        this.request = request;
        this.cVurlConfig = cVurlConfig;
        this.uri = uri;
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode) {
        return removeProxiesAfterAction(request.asyncAsObject(type, statusCode));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type, int statusCode) {
        return removeProxiesAfterAction(request.asyncAsObject(type, statusCode));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(Class<T> type) {
        return removeProxiesAfterAction(request.asyncAsObject(type));
    }

    @Override
    public <T> CompletableFuture<T> asyncAsObject(BodyType<T> type) {
        return removeProxiesAfterAction(request.asyncAsObject(type));
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString() {
        return removeProxiesAfterAction(request.asyncAsString());
    }

    @Override
    public CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph) {
        return removeProxiesAfterAction(request.asyncAsString(pph));
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream() {
        return removeProxiesAfterAction(request.asyncAsStream());
    }

    @Override
    public CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph) {
        return removeProxiesAfterAction(request.asyncAsStream(pph));
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler) {
        return removeProxiesAfterAction(request.asyncAs(bodyHandler));
    }

    @Override
    public <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph) {
        return removeProxiesAfterAction(request.asyncAs(bodyHandler, pph));
    }

    @Override
    public <T> Optional<T> asObject(Class<T> type, int statusCode) {
        return removeProxiesAfterAction(() -> request.asObject(type, statusCode));
    }

    @Override
    public <T> Optional<T> asObject(BodyType<T> type, int statusCode) {
        return removeProxiesAfterAction(() -> request.asObject(type, statusCode));
    }

    @Override
    public <T> T asObject(Class<T> type) {
        return removeProxiesAfterAction(() -> request.asObject(type));
    }

    @Override
    public <T> T asObject(BodyType<T> type) {
        return removeProxiesAfterAction(() -> request.asObject(type));
    }

    @Override
    public Optional<Response<String>> asString() {
        return removeProxiesAfterAction(request::asString);
    }

    @Override
    public Optional<Response<InputStream>> asStream() {
        return removeProxiesAfterAction(request::asStream);
    }

    @Override
    public <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler) {
        return removeProxiesAfterAction(() -> request.as(bodyHandler));
    }

    private <T> T removeProxiesAfterAction(Supplier<T> action) {
        try {
            return action.get();
        } finally {
            removeProxyIfPresent();
        }
    }

    private <T> CompletableFuture<T> removeProxiesAfterAction(CompletableFuture<T> future) {
        return future.whenComplete((result, exception) -> removeProxyIfPresent());
    }

    private void removeProxyIfPresent() {
        final Optional<ProxySelector> selectorOptional = cVurlConfig.getProxySelector();
        if (selectorOptional.isEmpty()) return;

        final ProxySelector proxySelector = selectorOptional.get();
        if (proxySelector instanceof CVurlProxySelector)
            ((CVurlProxySelector) proxySelector).removeProxiesForUri(uri);
    }
}
