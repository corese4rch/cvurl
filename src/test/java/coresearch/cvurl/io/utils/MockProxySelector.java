package coresearch.cvurl.io.utils;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MockProxySelector extends ProxySelector {

    public static MockProxySelector create(){
        return new MockProxySelector();
    }

    @Override
    public List<Proxy> select(URI uri) {
        return new ArrayList<>();
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // Implementation is not needed
    }
}
