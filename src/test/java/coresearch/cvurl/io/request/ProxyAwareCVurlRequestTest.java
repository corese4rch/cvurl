package coresearch.cvurl.io.request;

import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.model.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class ProxyAwareCVurlRequestTest {

    private final URI uri = URI.create("http://localhost:8080");

    private final Function<HttpRequest, HttpResponse.BodyHandler<String>> stringHandlerFunction =
            r -> HttpResponse.BodyHandlers.ofString();
    private final HttpResponse.PushPromiseHandler<String> stringPph = HttpResponse.PushPromiseHandler
            .of(stringHandlerFunction, new ConcurrentHashMap<>());

    private final Function<HttpRequest, HttpResponse.BodyHandler<InputStream>> streamHandlerFunction =
            r -> HttpResponse.BodyHandlers.ofInputStream();
    private final HttpResponse.PushPromiseHandler<InputStream> streamPph = HttpResponse.PushPromiseHandler
            .of(streamHandlerFunction, new ConcurrentHashMap<>());

    private ProxyAwareCVurlRequest request;
    private CVurlProxySelector proxySelector;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() {
        final CVurlConfig config = Mockito.mock(CVurlConfig.class);
        proxySelector = Mockito.mock(CVurlProxySelector.class);
        Mockito.when(config.getProxySelector()).thenReturn(Optional.of(proxySelector));

        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final CompletableFuture<HttpResponse<Object>> future = CompletableFuture.completedFuture(Mockito.mock(HttpResponse.class));
        Mockito.when(httpClient.sendAsync(Mockito.any(), Mockito.any())).thenReturn(future);
        Mockito.when(httpClient.sendAsync(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(future);

        Mockito.when(config.getHttpClient()).thenReturn(httpClient);
        Mockito.when(config.getGenericMapper()).thenReturn(Mockito.mock(GenericMapper.class));

        final CVurlRequest cvurlRequest = new CVurlRequest(
                Mockito.mock(HttpRequest.class),
                config,
                RequestConfiguration.builder().build()
        );

        request = new ProxyAwareCVurlRequest(cvurlRequest, config, uri);
    }

    @Test
    public void whenRequestMethodAsyncAsObjectClassWithStatusExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Object> future = request.asyncAsObject(Object.class, 0);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsObjectBodyTypeWithStatusExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Object> future = request.asyncAsObject(new BodyType<>() {}, 0);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsObjectClassExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Object> future = request.asyncAsObject(Object.class);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsObjectBodyTypeExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Object> future = request.asyncAsObject(new BodyType<>() {});

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsStringExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<String>> future = request.asyncAsString();

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsStringWithPphExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<String>> future = request.asyncAsString(stringPph);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsStreamExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<InputStream>> future = request.asyncAsStream();

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsStreamWithPphExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<InputStream>> future = request.asyncAsStream(streamPph);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsBodyHandlerExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<String>> future =
                request.asyncAs(HttpResponse.BodyHandlers.ofString());

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsyncAsBodyHandlerAndPphExecuted_removeProxiesForUriIsCalled() throws ExecutionException, InterruptedException {
        final CompletableFuture<Response<String>> future =
                request.asyncAs(HttpResponse.BodyHandlers.ofString(), stringPph);

        future.get();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsObjectClassWithStatusExecuted_removeProxiesForUriIsCalled() {
        request.asObject(Object.class, 0);

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsObjectBodyTypeWithStatusExecuted_removeProxiesForUriIsCalled() {
        request.asObject(new BodyType<>() {}, 0);

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsObjectClassExecuted_removeProxiesForUriIsCalled() {
        request.asObject(Object.class);

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsObjectBodyTypeExecuted_removeProxiesForUriIsCalled() {
        request.asObject(new BodyType<>() {});

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsStringExecuted_removeProxiesForUriIsCalled() {
        request.asString();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsStreamExecuted_removeProxiesForUriIsCalled() {
        request.asStream();

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    @Test
    public void whenRequestMethodAsExecuted_removeProxiesForUriIsCalled() {
        request.as(HttpResponse.BodyHandlers.ofString());

        verifyRemoveProxiesForUriCalledExactlyOnce();
    }

    private void verifyRemoveProxiesForUriCalledExactlyOnce() {
        Mockito.verify(proxySelector, Mockito.times(1)).removeProxiesForUri(uri);
    }
}
