package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpStatus;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CVurlPatchRequestTest extends AbstractRequestTest {

    private static final String TEST_BODY_FOR_TEST = "Test body for test";

    @Test
    public void sendPATCHTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_URLTest() throws MalformedURLException {

        //given
        String strURL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        URL url = new URL(strURL);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_QueryParamTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .queryParam(testParam, testParam)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_QueryParamsTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";
        String testParam2 = "param2";

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .queryParam(testParam, testParam)
                .queryParam(testParam2, testParam2)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_HeaderRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .header(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT))
                        .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_HeadersRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, TEST_TOKEN);
        headers.put(HttpHeader.ACCEPT, "xml");

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .withHeader(HttpHeader.ACCEPT, WireMock.equalTo("xml"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .headers(headers)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);
        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_checkResponseHeaderTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.headersNames().contains(HttpHeader.AUTHORIZATION));
        assertEquals(TEST_TOKEN, response.getHeaderValue(HttpHeader.AUTHORIZATION).orElseThrow());
    }

    @Test
    public void sendPATCH_StringResponseTest() {

        //given
        String body = TEST_BODY_FOR_TEST;
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        Response<String> response = cvurl.patch(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }


    @Test
    public void sendPATCH_StringRequestBodyTest() {

        //given
        String body = TEST_BODY_FOR_TEST;
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body(body)
                .asString()
                .orElseThrow(RuntimeException::new);
        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_BytesRequestBodyTest() {

        //given
        String body = TEST_BODY_FOR_TEST;
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body(body.getBytes())
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_CollectionsRequestBodyTest() throws JsonProcessingException {

        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(users)))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body(users)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPATCH_ObjectRequestBodyTest() throws JsonProcessingException {

        //given
        User user = ObjectGenerator.generateTestObject();
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.patch(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(user)))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.patch(url)
                .body(user)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.patchRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }
}
