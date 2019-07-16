package coresearch.cvurl.io.model;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ConfigurationTest {

    @Test
    public void builderTest() {
        //given
        var authenticator = mock(Authenticator.class);
        var connectTimeout = Duration.ofSeconds(1);
        var cookieHandler = mock(CookieHandler.class);
        var executor = mock(Executor.class);
        var followRedirects = mock(HttpClient.Redirect.class);
        var proxySelector = mock(ProxySelector.class);
        var sslContext = mock(SSLContext.class);
        var sslParameters = mock(SSLParameters.class);
        var priority = 1;
        var version = HttpClient.Version.HTTP_1_1;

        //when
        var conf = Configuration.builder()
                .authenticator(authenticator)
                .connectTimeout(connectTimeout)
                .cookieHandler(cookieHandler)
                .executor(executor)
                .followRedirects(followRedirects)
                .proxySelector(proxySelector)
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .priority(priority)
                .version(version)
                .build();

        //then
        assertSame(authenticator, conf.getAuthenticator());
        assertSame(connectTimeout, conf.getConnectTimeout());
        assertSame(cookieHandler, conf.getCookieHandler());
        assertSame(executor, conf.getExecutor());
        assertSame(followRedirects, conf.getFollowRedirects());
        assertSame(proxySelector, conf.getProxySelector());
        assertSame(sslContext, conf.getSslContext());
        assertSame(sslParameters, conf.getSslParameters());
        assertSame(version, conf.getVersion());
        assertEquals(priority, conf.getPriority());
    }

    @Test
    public void createHttpClientTest() {
        //given
        var authenticator = mock(Authenticator.class);
        var connectTimeout = Duration.ofSeconds(1);
        var cookieHandler = mock(CookieHandler.class);
        var executor = mock(Executor.class);
        var followRedirects = mock(HttpClient.Redirect.class);
        var proxySelector = mock(ProxySelector.class);
        var sslContext = mock(SSLContext.class);
        var sslParameters = new SSLParameters(new String[]{"test"});
        var priority = 1;
        var version = HttpClient.Version.HTTP_1_1;

        var conf = Configuration.builder()
                .authenticator(authenticator)
                .connectTimeout(connectTimeout)
                .cookieHandler(cookieHandler)
                .executor(executor)
                .followRedirects(followRedirects)
                .proxySelector(proxySelector)
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .priority(priority)
                .version(version)
                .build();

        //when
        var httpClient = conf.createHttpClient();
        //then
        assertSame(authenticator, httpClient.authenticator().get());
        assertSame(connectTimeout, httpClient.connectTimeout().get());
        assertSame(cookieHandler, httpClient.cookieHandler().get());
        assertSame(executor, httpClient.executor().get());
        assertSame(followRedirects, httpClient.followRedirects());
        assertSame(proxySelector, httpClient.proxy().get());
        assertSame(sslContext, httpClient.sslContext());
        assertArrayEquals(sslParameters.getCipherSuites(), httpClient.sslParameters().getCipherSuites());
        assertSame(version, httpClient.version());
    }

}