package coresearch.cvurl.io.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

class CVurlProxySelectorTest {

    private static final String REQUEST_URL = "https://test-url.com/";
    private static final URI REQUEST_URI = URI.create(REQUEST_URL);
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 8080;

    private CVurlProxySelector selector;

    @BeforeEach
    void setupCvurlProxySelector() {
        selector = new CVurlProxySelector();
    }

    @Test
    void whenSpecifiedProxiesForMultipleURIs_returnProxiesOnlyForRequestedURI() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL + 2, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(expectedProxies, proxies);
    }

    @Test
    void whenAddedCVurlProxyForUri_returnCorrectProxyForSameUri() {
        // given
        final Proxy expectedProxy = makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT);
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        //then
        Assertions.assertEquals(1, proxies.size());
        Assertions.assertEquals(expectedProxy, proxies.get(0));
    }

    @Test
    void whenAddedTwoCvurlProxiesForSameUri_returnTwoCorrectProxiesForSameUri() {
        // given
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT),
                makeExpectedHttpProxy(PROXY_HOST + 2, PROXY_PORT));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        //then
        Assertions.assertEquals(expectedProxies, proxies);
    }

    @Test
    void whenProxySelectorSpecifiedAndNoPerRequestProxy_returnProxiesFromProxySelector() {
        // given
        final List<Proxy> expectedProxies = List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT));
        selector = new CVurlProxySelector(new ProxySelectorMock(expectedProxies));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertSame(expectedProxies, proxies);
    }

    @Test
    void whenProxySelectorSpecifiedWithPerRequestProxy_returnPerRequestProxy() {
        // given
        selector = new CVurlProxySelector(new ProxySelectorMock(List.of(makeExpectedHttpProxy(PROXY_HOST, PROXY_PORT))));
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST + 2, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertEquals(1, proxies.size());
        Assertions.assertEquals(makeExpectedHttpProxy(PROXY_HOST + 2, PROXY_PORT), proxies.get(0));
    }

    @Test
    void whenNoProxySelectorAndNoPerRequestProxySpecified_returnDefaultProxy() {
        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertSame(ProxySelector.getDefault().select(REQUEST_URI), proxies);
    }

    @Test
    void whenRemoveProxyForUriCalled_returnDefaultProxyForThisURI() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST, PROXY_PORT));
        selector.removeProxiesForUri(REQUEST_URI);

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertSame(ProxySelector.getDefault().select(REQUEST_URI), proxies);
    }

    @Test
    void whenCvurlProxyTypeHttp_returnProxyWithTypeHttp() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.HTTP, PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertSame(Proxy.Type.HTTP, proxies.get(0).type());
    }

    @Test
    void whenCvurlProxyTypeSocks_returnProxyWithTypeSocks() {
        // given
        selector.addProxy(REQUEST_URL, CVurlProxy.of(CVurlProxy.Type.SOCKS, PROXY_HOST, PROXY_PORT));

        // when
        final List<Proxy> proxies = selector.select(REQUEST_URI);

        // then
        Assertions.assertSame(Proxy.Type.SOCKS, proxies.get(0).type());
    }

    @Test
    void whenNoProxyIsUsed_returnNoProxy() {
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

    private static final class ProxySelectorMock extends ProxySelector {
        private final List<Proxy> expectedProxies;

        public ProxySelectorMock(List<Proxy> expectedProxies) {
            this.expectedProxies = expectedProxies;
        }

        @Override
        public List<Proxy> select(URI uri) {
            return expectedProxies;
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            // do nothing
        }
    }

}