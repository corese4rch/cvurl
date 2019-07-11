package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import coresearch.cvurl.io.constant.HttpStatus;
import coresearch.cvurl.io.exception.MappingException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.model.Response;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


public class CVurlRequestTest extends AbstractRequestTest {

    private static final String EMPTY_STRING = "";
    private static String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

    @Test
    public void emptyResponseTest() {

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(EMPTY_STRING)));

        Response<String> response = cvurl.GET(url).build().asString().orElseThrow(RuntimeException::new);

        assertEquals(EMPTY_STRING, response.getBody());
    }

    @Test
    public void mappingExceptionTest() {

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody("not a json string")));

        assertThrows(MappingException.class, () -> cvurl.GET(url).build().asObject(User.class, HttpStatus.OK));
    }

    @Test
    public void asObjectTest() throws JsonProcessingException {
        User user = ObjectGenerator.generateTestObject();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.GET(url).build().asObject(User.class, HttpStatus.OK);

        assertEquals(user, resultUser);
    }

    @Test
    public void asyncAsStringTest() throws ExecutionException, InterruptedException {

        String body = "I am a string";
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(body)));

        Response<String> response = cvurl.GET(url).build().asyncAsString()
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        assertTrue(isThenApplyInvoked[0]);
        assertEquals(body, response.getBody());
    }


    @Test
    public void asyncAsObjectTest() throws JsonProcessingException, ExecutionException, InterruptedException {

        User user = ObjectGenerator.generateTestObject();
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.GET(url).build().asyncAsObject(User.class, HttpStatus.OK)
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        assertTrue(isThenApplyInvoked[0]);
        assertEquals(user, resultUser);
    }

    @Test
    public void curlRequestTimeoutTest() {
        //given
        CVurl cvurl = new CVurl(Configuration.builder()
                .requestTimeout(Duration.ofMillis(100))
                .build());

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        Optional<Response<String>> response = cvurl.GET(url).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void requestTimeoutTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        Optional<Response<String>> response = cvurl.GET(url).timeout(Duration.ofMillis(100)).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void requestTimeoutOverridesCurlTimeoutTest() {
        //given
        CVurl cvurl = new CVurl(Configuration.builder()
                .requestTimeout(Duration.ofMillis(200))
                .build());

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        Optional<Response<String>> response = cvurl.GET(url).timeout(Duration.ofMillis(100)).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void failedRequestTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Optional<Response<String>> response = cvurl.GET(url).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void differentResponseStatusCodeTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST)));

        //when
        assertThrows(UnexpectedResponseException.class,
                () -> cvurl.GET(url).build().asObject(User.class, HttpStatus.OK));
    }

    @Test
    public void mapTest() {
        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        Response<List<String>> response = cvurl.GET(url).build().map(List::of).orElseThrow(RuntimeException::new);

        //then
        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody().get(0));
    }

    @Test
    public void asyncMapTest() throws ExecutionException, InterruptedException {
        //
        String body = "I am a string";
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(body)));

        Response<List<String>> response = cvurl.GET(url).build().asyncMap(List::of)
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        assertTrue(isThenApplyInvoked[0]);
        assertEquals(body, response.getBody().get(0));
    }

    @Test
    public void urlWithParametersAsURLTest() throws MalformedURLException {
        //given
        var params = "?params=1";
        var urlWithParameters = url + params;

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT + params + "&param2=2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.GET(URI.create(urlWithParameters).toURL())
                .queryParam("param2", "2")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(response.status(), HttpStatus.OK);
    }

    @Test
    public void urlWithParametersAsStringTest() {
        //given
        var params = "?param1=1";
        var urlWithParameters = url + params;

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT + params + "&param2=2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.GET(urlWithParameters)
                .queryParam("param2", "2")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(response.status(), HttpStatus.OK);
    }

    @Test
    public void responseWithStatusCode204AndNoContentLengthHeaderTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NO_CONTENT)));

        //when
        var response = cvurl.GET(url).build().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(response.status(), HttpStatus.NO_CONTENT);
    }
}
