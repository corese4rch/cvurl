package coresearch.cvurl.io.model;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.mapper.impl.JacksonMapper;
import coresearch.cvurl.io.utils.MockHttpClient;
import coresearch.cvurl.io.utils.MockProxySelector;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    public void builderTest() throws NoSuchAlgorithmException {
        //given
        var authenticator = new Authenticator() {};
        var connectTimeout = Duration.ofSeconds(1);
        var cookieHandler = new CookieManager();
        var executor = Executors.newFixedThreadPool(1);
        var followRedirects = HttpClient.Redirect.NEVER;
        var proxySelector = MockProxySelector.create();
        var sslContext = SSLContext.getDefault();
        var sslParameters = new SSLParameters(new String[]{"test"});
        var priority = 1;
        var version = HttpClient.Version.HTTP_1_1;

        //when
        var conf = CvurlConfig.builder()
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
    public void createHttpClientTest() throws NoSuchAlgorithmException {
        //given
        var authenticator = new Authenticator() {};
        var connectTimeout = Duration.ofSeconds(1);
        var cookieHandler = new CookieManager();
        var executor = Executors.newFixedThreadPool(1);
        var followRedirects = HttpClient.Redirect.NEVER;
        var proxySelector = MockProxySelector.create();
        var sslContext = SSLContext.getDefault();
        var sslParameters = new SSLParameters(new String[]{"test"});
        var priority = 1;
        var version = HttpClient.Version.HTTP_1_1;

        var conf = CvurlConfig.builder()
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
        var httpClient = MockHttpClient.create();
        var genericMapper = MapperFactory.createDefault();
        var requestTimeout = Duration.ofSeconds(42);
        var acceptCompressed = true;

        //when
        var configuration = CvurlConfig.builder(httpClient)
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

    @Test
    public void defaultConfigurationTest() {
        //when
        var configuration = CvurlConfig.defaultConfiguration();

        //then
        assertTrue(httpClientsEquals(configuration.getHttpClient(), HttpClient.newHttpClient()));
        assertSame(configuration.getGenericMapper().getClass(), JacksonMapper.class);
        assertSame(configuration.getHttpClientMode(), HttpClientMode.PROTOTYPE);
        assertTrue(requestConfigurationsEquals(
                configuration.getGlobalRequestConfiguration(), RequestConfiguration.defaultConfiguration()));
    }

    @Test
    public void preconfiguredBuilderTest() {
        //given
        var configuration = CvurlConfig.defaultConfiguration();

        //when
        var configurationBuilder = configuration.preconfiguredBuilder();

        //then
        var resultConfiguration = configurationBuilder.build();

        assertTrue(httpClientsEquals(configuration.getHttpClient(), resultConfiguration.getHttpClient()));
        assertSame(configuration.getGenericMapper().getClass(), resultConfiguration.getGenericMapper().getClass());
        assertSame(configuration.getHttpClientMode(), resultConfiguration.getHttpClientMode());
        assertTrue(requestConfigurationsEquals(
                configuration.getGlobalRequestConfiguration(), resultConfiguration.getGlobalRequestConfiguration()));
    }

    @Test
    public void setLogEnabledIsMutableTest() {
        //given
        var configuration = CvurlConfig.defaultConfiguration();

        //when
        configuration.setIsLogEnable(true);

        //then
        assertTrue(configuration.getGlobalRequestConfiguration().isLogEnabled());
    }

    @Test
    public void configurationBuilderTest() {
        //given
        var httpClient = MockHttpClient.create();
        var genericMapper = MapperFactory.createDefault();
        var clientMode = HttpClientMode.PROTOTYPE;
        var timeout = Duration.ofSeconds(1);
        var acceptCompressed = true;
        var logEnabled = true;

        //when
        var configuration = CvurlConfig.builder(httpClient)
                .genericMapper(genericMapper)
                .httpClientMode(clientMode)
                .requestTimeout(timeout)
                .acceptCompressed(acceptCompressed)
                .logEnabled(logEnabled)
                .build();

        //then
        assertSame(configuration.getHttpClient(), httpClient);
        assertSame(configuration.getGenericMapper(), genericMapper);
        assertEquals(configuration.getGlobalRequestConfiguration().getRequestTimeout()
                .orElseThrow(RuntimeException::new), timeout);
        assertEquals(configuration.getGlobalRequestConfiguration().isAcceptCompressed(), acceptCompressed);
        assertEquals(configuration.getGlobalRequestConfiguration().isLogEnabled(), logEnabled);
    }

    private boolean httpClientsEquals(HttpClient client1, HttpClient client2) {
        return optionalsEqual(client1.authenticator(), client2.authenticator()) &&
                optionalsEqual(client1.connectTimeout(), client2.connectTimeout()) &&
                optionalsEqual(client1.cookieHandler(), client2.cookieHandler()) &&
                optionalsEqual(client1.executor(), client2.executor()) &&
                optionalsEqual(client1.proxy(), client2.proxy()) &&
                client1.followRedirects().equals(client2.followRedirects()) &&
                client1.sslContext().equals(client2.sslContext()) &&
                Arrays.equals(client1.sslParameters().getCipherSuites(), client2.sslParameters().getCipherSuites()) &&
                client1.version().equals(client2.version());

    }

    private boolean requestConfigurationsEquals(RequestConfiguration conf1, RequestConfiguration conf2) {
        return optionalsEqual(conf1.getRequestTimeout(), conf2.getRequestTimeout()) &&
                conf1.isAcceptCompressed() == conf2.isAcceptCompressed() &&
                conf1.isLogEnabled() == conf2.isLogEnabled();
    }

    private <T> boolean optionalsEqual(Optional<T> opt1, Optional<T> opt2) {
        return opt1.map(t -> t.equals(opt2.get()))
                .orElseGet(() -> !opt2.isPresent());
    }
}