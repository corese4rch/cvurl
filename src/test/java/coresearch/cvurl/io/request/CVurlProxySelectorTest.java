package coresearch.cvurl.io.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

public class CVurlProxySelectorTest {

    private static final String REQUEST_URL = "https://test-url.com/";
    private static final URI REQUEST_URI = URI.create(REQUEST_URL);
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 8080;

    private CVurlProxySelector selector;

    @BeforeEach
    public void setupCvurlProxySelector() {
        selector = new CVurlProxySelector();
    }

    @Test
    public void whenSpecifiedProxiesForMultipleURIs_returnProxiesOnlyForRequestedURI() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL + 2, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(expectedProxies, proxies);
    }

    @Test
    public void whenAddedCVurlProxyForUri_returnCorrectProxyForSameUri() {
        // given
        final Proxy expectedProxy = makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT);
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        //then
        Assertions.assertEquals(1, proxies.size());
        Assertions.assertEquals(expectedProxy, proxies.get(0));
    }

    @Test
    public void whenAddedTwoCvurlProxiesForSameUri_returnTwoCorrectProxiesForSameUri() {
        // given
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT),
                makeExpectedHttpProxy(PROXY_HOST + 2, PROXY_PORT));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        //then
        Assertions.assertEquals(expectedProxies, proxies);
    }

    @Test
    public void whenProxySelectorSpecifiedAndNoPerRequestProxy_returnProxiesFromProxySelector() {
        // given
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT));
        selector = new CVurlProxySelector(new ProxySelectorMock(expectedProxies));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(expectedProxies, proxies);
    }

    @Test
    public void whenProxySelectorSpecifiedWithPerRequestProxy_returnPerRequestProxy() {
        // given
        selector = new CVurlProxySelector(new ProxySelectorMock(List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT))));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(1, proxies.size());
        Assertions.assertEquals(makeExpectedHttpProxy(PROXY_HOST + 2, PROXY_PORT), proxies.get(0));
    }

    @Test
    public void whenNoProxySelectorAndNoPerRequestProxySpecified_returnDefaultProxy() {
        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(ProxySelector.getDefault().select(REQUEST_URI), proxies);
    }

    @Test
    public void whenSelectForUriCalledTwice_returnDefaultProxyForThisUriForSecondCallForSameUri() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(Proxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.select(REQUEST_URI);

        // when
        final List<Proxy> proxiesSecond = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(ProxySelector.getDefault().select(REQUEST_URI), proxiesSecond);
    }

    @Test
    public void whenNoProxyIsUsed_returnNoProxy() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.noProxy());

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(List.of(Proxy.NO_PROXY), proxies);
    }

    private Proxy makeExpectedHttpProxy(String host, int port) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

}