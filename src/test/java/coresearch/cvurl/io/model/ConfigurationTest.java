package coresearch.cvurl.io.model;

import coresearch.cvurl.io.mapper.GenericMapper;
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
        var sslParameters = new SSLParameters(new String[]{"test"});
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
        assertSame(authenticator, conf.getAuthenticator().orElseThrow(RuntimeException::new));
        assertSame(connectTimeout, conf.getConnectTimeout().orElseThrow(RuntimeException::new));
        assertSame(cookieHandler, conf.getCookieHandler().orElseThrow(RuntimeException::new));
        assertSame(executor, conf.getExecutor().orElseThrow(RuntimeException::new));
        assertSame(followRedirects, conf.getFollowRedirects());
        assertSame(proxySelector, conf.getProxySelector().orElseThrow(RuntimeException::new));
        assertSame(sslContext, conf.getSslContext());
        assertArrayEquals(sslParameters.getCipherSuites(), conf.getSslParameters().getCipherSuites());
        assertSame(version, conf.getVersion());
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

    @Test
    public void httpClientBasedBuilderTest() {
        //given
        var httpClient = mock(HttpClient.class);
        var genericMapper = mock(GenericMapper.class);
        var requestTimeout = mock(Duration.class);
        var acceptCompressed = true;

        //when
        var configuration = Configuration.builder(httpClient)
                .genericMapper(genericMapper)
                .requestTimeout(requestTimeout)
                .acceptCompressed(acceptCompressed)
                .build();

        //then
        assertSame(configuration.getHttpClient(), httpClient);
        assertSame(configuration.getGenericMapper(), genericMapper);
        assertSame(configuration.getGlobalRequestConfiguration().getRequestTimeout().orElseThrow(RuntimeException::new),
                requestTimeout);
        assertEquals(configuration.getGlobalRequestConfiguration().isAcceptCompressed(), acceptCompressed);
    }

}