package coresearch.cvurl.io.utils;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public class MockProxySelector extends ProxySelector {

    private MockProxySelector() {
    }

    public static MockProxySelector create(){
        return new MockProxySelector();
    }

    @Override
    public List<Proxy> select(URI uri) {
        return null;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

    }
}
