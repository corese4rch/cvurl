package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpStatus;
import coresearch.cvurl.io.constant.MIMEType;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CVurlDeleteRequestTest extends AbstractRequestTest {

    @Test
    public void sendDeleteTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url).asString().orElseThrow(RuntimeException::new);
        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_URLTest() throws MalformedURLException {

        //given
        String strURL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        URL url = new URL(strURL);

        //when
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url).asString().orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_QueryParamTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .queryParam(testParam, testParam)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_HeaderRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .header(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT))
                        .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_QueryParamsTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";
        String testParam2 = "param2";

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .queryParam(testParam, testParam)
                .queryParam(testParam2, testParam2)
                .asString()
                .orElseThrow(RuntimeException::new);


        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_HeadersRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, TEST_TOKEN);
        headers.put(HttpHeader.ACCEPT, "xml");

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .withHeader(HttpHeader.ACCEPT, WireMock.equalTo("xml"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .headers(headers)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_checkResponseHeaderTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url).asString().orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.headersNames().contains(HttpHeader.AUTHORIZATION));
        assertEquals(TEST_TOKEN, response.getHeaderValue(HttpHeader.AUTHORIZATION).get());
    }

    @Test
    public void sendDELETE_StringResponseTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        Response<String> response = cvurl.delete(url).asString().orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    public void sendDELETE_StringRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .body(body)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_BytesRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .body(body.getBytes())
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_CollectionsRequestBodyTest() throws JsonProcessingException {

        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(users)))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .body(users)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendDELETE_ObjectRequestBodyTest() throws JsonProcessingException {

        //given
        User user = ObjectGenerator.generateTestObject();
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.delete(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(user)))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.delete(url)
                .body(user)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.deleteRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }
}
