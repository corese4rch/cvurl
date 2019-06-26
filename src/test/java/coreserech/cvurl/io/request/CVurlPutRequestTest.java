package coreserech.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import coreserech.cvurl.io.helper.ObjectGenerator;
import coreserech.cvurl.io.helper.model.User;
import coreserech.cvurl.io.model.Response;
import coreserech.cvurl.io.util.HttpHeader;
import coreserech.cvurl.io.util.HttpStatus;
import coreserech.cvurl.io.util.MIMEType;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVurlPutRequestTest extends AbstractRequestTest {

    @Test
    public void sendPUTTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_URLTest() throws MalformedURLException {

        //given
        String strURL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        URL url = new URL(strURL);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_QueryParamTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .queryParam(testParam, testParam)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param")));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_QueryParamsTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        String testParam = "param";
        String testParam2 = "param2";

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .queryParam(testParam, testParam)
                .queryParam(testParam2, testParam2)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT + "?param=param&param2=param2")));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_HeaderRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .header(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT))
                        .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_HeadersRequiredTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION, TEST_TOKEN);
        headers.put(HttpHeader.ACCEPT, "xml");

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.AUTHORIZATION, WireMock.equalTo(TEST_TOKEN))
                .withHeader(HttpHeader.ACCEPT, WireMock.equalTo("xml"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .headers(headers)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_checkResponseHeaderTest() {

        //given
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeader.AUTHORIZATION, TEST_TOKEN)
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
        Assert.assertTrue(response.headersNames().contains(HttpHeader.AUTHORIZATION));
        Assert.assertEquals(TEST_TOKEN, response.getHeaderValue(HttpHeader.AUTHORIZATION).get());
    }

    @Test
    public void sendPUT_StringResponseTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        Response<String> response = cvurl.PUT(url)
                .body("")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
        Assert.assertEquals(body, response.getBody());
    }

    @Test
    public void sendPUT_StringRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body(body)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_BytesRequestBodyTest() {

        //given
        String body = "Test body for test";
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body(body.getBytes())
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_CollectionsRequestBodyTest() throws JsonProcessingException {

        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(users)))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body(users)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_ObjectRequestBodyTest() throws JsonProcessingException {

        //given
        User user = ObjectGenerator.generateTestObject();
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(mapper.writeValueAsString(user)))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body(user)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendPUT_JSONRequestBodyTest() {

        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("status", 200);
        String url = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

        //when
        wiremock.stubFor(WireMock.put(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withRequestBody(WireMock.equalTo(jsonObject.toString()))
                .withHeader(HttpHeader.CONTENT_TYPE, WireMock.equalTo(MIMEType.APPLICATION_JSON))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)));

        Response<String> response = cvurl.PUT(url)
                .body(jsonObject)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        WireMock.verify(WireMock.exactly(1),
                WireMock.putRequestedFor(WireMock.urlEqualTo(TEST_ENDPOINT)));

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(HttpStatus.OK, response.status());
    }
}
