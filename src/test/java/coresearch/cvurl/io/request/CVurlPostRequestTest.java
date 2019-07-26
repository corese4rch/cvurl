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

public class CVurlPostRequestTest extends AbstractRequestTest {

    @Test
    public void sendPOSTTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_URLTest() throws MalformedURLException {

        //given
        String strURL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        URL url = new URL(strURL);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_QueryParamTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .queryParam(testParam, testParam)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_QueryParamsTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";
        String testParam2 = "param2";

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .queryParam(testParam, testParam)
                .queryParam(testParam2, testParam2)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_HeaderRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .header(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT))
                        .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_HeadersRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, TEST_TOKEN);
        headers.put(HttpHeader.ACCEPT, "xml");

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .withHeader(HttpHeader.ACCEPT, WireMock.equalTo("xml"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .headers(headers)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_checkResponseHeaderTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.headersNames().contains(HttpHeader.AUTHORIZATION));
        assertEquals(TEST_TOKEN, response.getHeaderValue(HttpHeader.AUTHORIZATION).get());
    }

    @Test
    public void sendPOST_StringResponseTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        Response<String> response = cvurl.post(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }


    @Test
    public void sendPOST_StringRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body(body)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_BytesRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body(body.getBytes())
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_CollectionsRequestBodyTest() throws JsonProcessingException {

        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(users)))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body(users)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPOST_ObjectRequestBodyTest() throws JsonProcessingException {

        //given
        User user = ObjectGenerator.generateTestObject();
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(user)))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.post(url)
                .body(user)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.postRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }
}
