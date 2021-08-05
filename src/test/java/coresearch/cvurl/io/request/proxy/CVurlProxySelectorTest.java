package coresearch.cvurl.io.request.proxy;

import coresearch.cvurl.io.model.CVurlProxy;
import coresearch.cvurl.io.utils.ProxySelectorMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CVurlProxySelectorTest {

    private static final String REQUEST_URL = "https://test-url.com/";
    private static final URI REQUEST_URI = URI.create(REQUEST_URL);
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 8181;

    private CVurlProxySelector selector;

    @BeforeEach
    public void setupCvurlProxySelector() {
        this.selector = new CVurlProxySelector();
    }

    @Test
    void shouldReturnProxiesOnlyForRequestedURIWhenSpecifiedProxiesForMultipleURIs() {
        //given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL + 2, CVurlProxy.of(PROXY_HOST + 2, PROXY_PORT));
        final var expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST));

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(expectedProxies, proxies);
    }

    @Test
    void shouldReturnCorrectProxyForSameUriWhenAddedCVurlProxyForUri() {
        //given
        final var expectedProxy = makeExpectedHttpProxy(PROXY_HOST);
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST, PROXY_PORT));

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(1, proxies.size());
        assertEquals(expectedProxy, proxies.get(0));
    }

    @Test
    void shouldReturnTwoCorrectProxiesForSameUriWhenAddedTwoCvurlProxiesForSameUri() {
        //given
        final var expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST),
                makeExpectedHttpProxy(PROXY_HOST + 2));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST + 2, PROXY_PORT));

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(expectedProxies, proxies);
    }

    @Test
    void shouldReturnProxiesFromProxySelectorWhenProxySelectorSpecifiedAndNoPerRequestProxy() {
        //given
        final var expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST));
        selector = new CVurlProxySelector(new ProxySelectorMock(expectedProxies));

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(expectedProxies, proxies);
    }

    @Test
    void shouldReturnPerRequestProxyWhenProxySelectorSpecifiedWithPerRequestProxy() {
        //given
        selector = new CVurlProxySelector(new ProxySelectorMock(List.of(makeExpectedHttpProxy(PROXY_HOST))));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST + 2, PROXY_PORT));

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(1, proxies.size());
        assertEquals(makeExpectedHttpProxy(PROXY_HOST + 2), proxies.get(0));
    }

    @Test
    void shouldReturnDefaultProxyWhenNoProxySelectorAndNoPerRequestProxySpecified() {
        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(ProxySelector.getDefault().select(REQUEST_URI), proxies);
    }

    @Test
    void shouldReturnDefaultProxyForThisUriForSecondCallForSameUriWhenSelectForUriCalledTwice() {
        //given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(PROXY_HOST, PROXY_PORT));
        selector.select(REQUEST_URI);

        //when
        final var proxiesSecond = selector.select(REQUEST_URI);

        //then
        assertEquals(ProxySelector.getDefault().select(REQUEST_URI), proxiesSecond);
    }

    @Test
    void shouldReturnNoProxyWhenNoProxyIsUsed() {
        //given
        selector.addProxy(REQUEST_URL, CVurlProxy.noProxy());

        //when
        final var proxies = selector.select(REQUEST_URI);

        //then
        assertEquals(List.of(Proxy.NO_PROXY), proxies);
    }

    private Proxy makeExpectedHttpProxy(String host) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, CVurlProxySelectorTest.PROXY_PORT));
    }

}