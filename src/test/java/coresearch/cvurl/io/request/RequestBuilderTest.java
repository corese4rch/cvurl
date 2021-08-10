package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.model.CVurlProxy;
import coresearch.cvurl.io.request.proxy.CVurlProxySelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ProxySelector;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
class RequestBuilderTest {

    private static final CVurlProxy CVURL_PROXY = CVurlProxy.of("proxy.host", 90);
    private static final String uri = "http://localhost:8181/";
    private CVurlProxySelector proxySelectorSpy;
    private RequestBuilder builder;
    private CVurlConfig config;

    @BeforeEach
    public void setup() {
        this.proxySelectorSpy = spy(CVurlProxySelector.class);
        this.config = mock(CVurlConfig.class);
        when(config.getGlobalRequestConfiguration()).thenReturn(new RequestConfiguration());
        this.builder = new RequestBuilder(uri, HttpMethod.GET, config);
    }

    @Test
    void shouldAddNewProxyWhenProxySelectorIsNotEmpty() {
        //given
        when(config.getProxySelector()).thenReturn(Optional.of(proxySelectorSpy));

        //when
        builder.proxy(CVURL_PROXY);

        //then
        verifyAddProxyCalled(1);
    }

    @Test
    void doNothingWhenProxySelectorIsEmpty() {
        //given
        when(config.getProxySelector()).thenReturn(Optional.empty());

        //when
        builder.proxy(CVURL_PROXY);

        //then
        verifyAddProxyCalled(0);
    }

    @Test
    void doNothingWhenProxySelectorIsNotInstanceOfCVurlProxySelector() {
        //given
        when(config.getProxySelector()).thenReturn(Optional.of(ProxySelector.getDefault()));

        //when
        builder.proxy(CVURL_PROXY);

        //then
        verifyAddProxyCalled(0);
    }

    private void verifyAddProxyCalled(int times) {
        verify(proxySelectorSpy, times(times)).addProxy(uri, CVURL_PROXY);
    }
}