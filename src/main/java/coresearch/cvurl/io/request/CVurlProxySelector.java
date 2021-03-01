package coresearch.cvurl.io.request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class CVurlProxySelector extends ProxySelector {

    private final Map<String, List<Proxy>> proxiesByUri = new ConcurrentHashMap<>();
    private final ProxySelector proxySelector;

    public CVurlProxySelector() {
        this(null);
    }

    public CVurlProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    @Override
    public List<Proxy> select(URI uri) {
        final String stringUri = uri.toString();
        // proxy-per-request
        final List<Proxy> proxies = new ArrayList<>(proxiesByUri.getOrDefault(stringUri, List.of()));
        proxiesByUri.remove(stringUri);

        // proxy-per-client
        if (proxies.isEmpty() && proxySelector != null)
            proxies.addAll(proxySelector.select(uri));

        // default proxy
        if (proxies.isEmpty())
            proxies.addAll(ProxySelector.getDefault().select(uri));

        return proxies;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // ignore for now
    }

    public void addProxy(String uri, CVurlProxy cVurlProxy) {
        proxiesByUri.computeIfAbsent(uri, k -> new ArrayList<>())
                .add(toProxy(cVurlProxy));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CVurlProxySelector that = (CVurlProxySelector) o;
        return Objects.equals(proxiesByUri, that.proxiesByUri) && Objects.equals(proxySelector, that.proxySelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxiesByUri, proxySelector);
    }

    private Proxy toProxy(CVurlProxy cVurlProxy) {
        if (cVurlProxy.getType() == CVurlProxyType.DIRECT)
            return Proxy.NO_PROXY;

        final InetSocketAddress sa = new InetSocketAddress(cVurlProxy.getHost(), cVurlProxy.getPort());
        return new Proxy(cVurlProxy.getType().getJavaProxyType(), sa);
    }

}
