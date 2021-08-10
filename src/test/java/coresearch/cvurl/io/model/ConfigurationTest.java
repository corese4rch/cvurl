package coresearch.cvurl.io.model;

import coresearch.cvurl.io.constant.HttpClientMode;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.mapper.impl.JacksonMapper;
import coresearch.cvurl.io.request.proxy.CVurlProxySelector;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationTest {

    @Test
    void shouldReturnValidCVurlConfigWhenBuilderIsUsed() throws NoSuchAlgorithmException {
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
        var conf = CVurlConfig.builder()
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
        assertEquals(new CVurlProxySelector(proxySelector), conf.getProxySelector().orElseThrow(RuntimeException::new));
        assertSame(sslContext, conf.getSslContext());
        assertArrayEquals(sslParameters.getCipherSuites(), conf.getSslParameters().getCipherSuites());
        assertSame(version, conf.getVersion());
    }

    @Test
    void shouldReturnValidCVurlConfigWhenBuilderWithHttpClientIsUsed() {
        //given
        var httpClient = MockHttpClient.create();
        var genericMapper = MapperFactory.createDefault();
        var requestTimeout = Duration.ofSeconds(42);

        //when
        var configuration = CVurlConfig.builder(httpClient)
                .genericMapper(genericMapper)
                .requestTimeout(requestTimeout)
                .acceptCompressed(true)
                .build();

        //then
        assertSame(httpClient, configuration.getHttpClient());
        assertSame(genericMapper, configuration.getGenericMapper());
        assertSame(requestTimeout, configuration.getGlobalRequestConfiguration().getRequestTimeout().orElseThrow(RuntimeException::new));
        assertTrue(configuration.getGlobalRequestConfiguration().isAcceptCompressed());
    }

    @Test
    void shouldReturnValidCVurlConfigurationWhenDefaultConfigurationMethodIsUsed() {
        //given
        final HttpClient httpClient = HttpClient.newBuilder()
                .proxy(new CVurlProxySelector())
                .build();

        //when
        var configuration = CVurlConfig.defaultConfiguration();

        //then
        assertTrue(httpClientsEquals(configuration.getHttpClient(), httpClient));
        assertSame(JacksonMapper.class, configuration.getGenericMapper().getClass());
        assertSame(HttpClientMode.PROTOTYPE, configuration.getHttpClientMode());
        assertTrue(requestConfigurationsEquals(
                configuration.getGlobalRequestConfiguration(), RequestConfiguration.defaultConfiguration()));
    }

    @Test
    void shouldReturnValidCVurlConfigurationWhenPreconfiguredBuilderMethodIsUsed() {
        //given
        var configuration = CVurlConfig.defaultConfiguration();

        //when
        var resultConfiguration = configuration.preconfiguredBuilder().build();

        //then
        assertTrue(httpClientsEquals(configuration.getHttpClient(), resultConfiguration.getHttpClient()));
        assertSame(configuration.getGenericMapper().getClass(), resultConfiguration.getGenericMapper().getClass());
        assertSame(configuration.getHttpClientMode(), resultConfiguration.getHttpClientMode());
        assertTrue(requestConfigurationsEquals(
                configuration.getGlobalRequestConfiguration(), resultConfiguration.getGlobalRequestConfiguration()));
    }

    @Test
    void shouldChangeLogEnabledValueToTrueUsingDeprecatedMethod() {
        //given
        var configuration = CVurlConfig.defaultConfiguration();

        //when
        configuration.setIsLogEnable(true);

        //then
        assertTrue(configuration.getGlobalRequestConfiguration().isLogEnabled());
    }

    @Test
    void shouldChangeLogEnabledValueToTrue() {
        //given
        var configuration = CVurlConfig.defaultConfiguration();

        //when
        configuration.setLogEnabled(true);

        //then
        assertTrue(configuration.getGlobalRequestConfiguration().isLogEnabled());
    }

    @Test
    void shouldReturnValidCVurlConfigWhenBuilderWithHttpClientIsUsedAndClientModeIsPrototype() {
        //given
        var httpClient = MockHttpClient.create();
        var genericMapper = MapperFactory.createDefault();
        var clientMode = HttpClientMode.PROTOTYPE;
        var timeout = Duration.ofSeconds(1);

        //when
        var configuration = CVurlConfig.builder(httpClient)
                .genericMapper(genericMapper)
                .httpClientMode(clientMode)
                .requestTimeout(timeout)
                .acceptCompressed(true)
                .logEnabled(true)
                .build();

        //then
        assertSame(httpClient, configuration.getHttpClient());
        assertSame(genericMapper, configuration.getGenericMapper());
        assertEquals(timeout, configuration.getGlobalRequestConfiguration().getRequestTimeout()
                .orElseThrow(RuntimeException::new));
        assertTrue(configuration.getGlobalRequestConfiguration().isAcceptCompressed());
        assertTrue(configuration.getGlobalRequestConfiguration().isLogEnabled());
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
        return opt1.map(t -> opt2.isPresent() && t.equals(opt2.get()))
                .orElseGet(opt2::isEmpty);
    }
}