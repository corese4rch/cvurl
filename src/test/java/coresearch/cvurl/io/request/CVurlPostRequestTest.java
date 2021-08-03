package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpStatus;
import coresearch.cvurl.io.constant.MIMEType;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CVurlPostRequestTest extends AbstractRequestTest {

    private static final String TEST_BODY_FOR_TEST = "Test body for test";

    @Test
    void shouldReturnStatusCodeOkWhenRequestIsValid() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenUrlIsValid() throws MalformedURLException {
        //given
        var strURL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        var url = new URL(strURL);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenQueryParameterIsSpecified() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        var testParam = "param";

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT + "?param=param"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .queryParam(testParam, testParam)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT + "?param=param")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenQueryParametersAreSpecified() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        var testParam = "param";
        var testParam2 = "param2";

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .queryParam(testParam, testParam)
                .queryParam(testParam2, testParam2)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2")));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenHeaderIsProvided() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, equalTo(TEST_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .header(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1),
                postRequestedFor(urlEqualTo(TEST_ENDPOINT)).withHeader(HttpHeader.AUTHORIZATION, equalTo(TEST_TOKEN)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenHeadersAreProvided() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        var headers = new HashMap<String, String>();
        headers.put(HttpHeader.AUTHORIZATION, TEST_TOKEN);
        headers.put(HttpHeader.ACCEPT, "xml");

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, equalTo(TEST_TOKEN))
                .withHeader(HttpHeader.ACCEPT, equalTo("xml"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .headers(headers)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnAuthorizationHeaderWhenRequestIsValid() {
        //given
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withHeader(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.headersNames().contains(HttpHeader.AUTHORIZATION));
        assertEquals(TEST_TOKEN, response.getHeaderValue(HttpHeader.AUTHORIZATION).orElseThrow());
    }

    @Test
    void shouldReturnStringBodyWhenRequestIsValid() {
        //given
        var body = TEST_BODY_FOR_TEST;
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //when
        var response = cVurl.post(url)
                .body("")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    void shouldReturnStatusCodeOkWhenStringRequestBody() {
        //given
        var body = TEST_BODY_FOR_TEST;
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(equalTo(body))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body(body)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenBodyInBytesIsSpecified() {
        //given
        var body = TEST_BODY_FOR_TEST;
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(equalTo(body))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body(body.getBytes())
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenBodyIsList() throws JsonProcessingException {
        //given
        var users = ObjectGenerator.generateListOfTestObjects();
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(equalTo(mapper.writeValueAsString(users)))
                .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body(users)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenBodyIsObject() throws JsonProcessingException {
        //given
        var user = ObjectGenerator.generateTestObject();
        var url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(equalTo(mapper.writeValueAsString(user)))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(url)
                .body(user)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        verify(exactly(1), postRequestedFor(urlEqualTo(TEST_ENDPOINT)));

        assertTrue(response.isSuccessful());
        assertEquals(HttpStatus.OK, response.status());
    }
}
