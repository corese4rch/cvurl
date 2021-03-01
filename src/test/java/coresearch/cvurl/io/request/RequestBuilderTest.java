package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.model.CVurlConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public class RequestBuilderTest {

    private static final CVurlProxy CVURL_PROXY = CVurlProxy.of(Proxy.Type.HTTP, "proxy.host", 90);
    private static final String uri = "http://localhost:8080/";
    private CVurlProxySelector proxySelectorSpy;
    private RequestBuilder builder;
    private CVurlConfig config;

    @BeforeEach
    public void setup() {
        proxySelectorSpy = Mockito.spy(CVurlProxySelector.class);
        config = Mockito.mock(CVurlConfig.class);
        Mockito.when(config.getGlobalRequestConfiguration()).thenReturn(new RequestConfiguration());
        builder = new RequestBuilder(uri, HttpMethod.GET, config);
    }

    @Test
    void whenHttpClientHasCVurlProxySelector_thenWithProxyInputsPassedToCVurlProxySelector() {
        Mockito.when(config.getProxySelector()).thenReturn(Optional.of(proxySelectorSpy));

        builder.withProxy(CVURL_PROXY);

        verifyAddProxyCalled(1);
    }

    @Test
    void whenHttpClientHasNoProxySelectorSpecified_thenWithProxyInputsIgnored() {
        Mockito.when(config.getProxySelector()).thenReturn(Optional.empty());

        builder.withProxy(CVURL_PROXY);

        verifyAddProxyCalled(0);
    }

    @Test
    void whenHttpClientHasNotCVurlProxySelectorSpecified_thenWithProxyInputsIgnored() {
        Mockito.when(config.getProxySelector()).thenReturn(Optional.of(ProxySelector.getDefault()));

        builder.withProxy(CVURL_PROXY);

        verifyAddProxyCalled(0);
    }

    private void verifyAddProxyCalled(int times) {
        Mockito.verify(proxySelectorSpy, Mockito.times(times)).addProxy(uri, CVURL_PROXY);
    }
}