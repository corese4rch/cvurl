package coresearch.cvurl.io.utils;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public final class ProxySelectorMock extends ProxySelector {

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
