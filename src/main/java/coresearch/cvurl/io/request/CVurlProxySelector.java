package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.UnhandledProxyTypeException;

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
        // proxy-per-request
        final List<Proxy> proxies = List.copyOf(proxiesByUri.getOrDefault(uri.toString(), List.of()));
        if (!proxies.isEmpty())
            return proxies;

        // proxy-per-client
        if (proxySelector != null)
            return proxySelector.select(uri);

        // default proxy
        return ProxySelector.getDefault().select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // ignore for now
    }

    public void addProxy(String uri, CVurlProxy cVurlProxy) {
        proxiesByUri.computeIfAbsent(uri, k -> new ArrayList<>())
                .add(toProxy(cVurlProxy));
    }

    public void removeProxiesForUri(URI uri) {
        proxiesByUri.remove(uri.toString());
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
        return new Proxy(toProxyType(cVurlProxy.getType()), sa);
    }

    private Proxy.Type toProxyType(CVurlProxyType type) {
        switch (type) {
            case HTTP: return Proxy.Type.HTTP;
            case SOCKS: return Proxy.Type.SOCKS;
            default: throw new UnhandledProxyTypeException(type);
        }
    }
}
