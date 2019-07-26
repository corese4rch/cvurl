package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import coresearch.cvurl.io.constant.*;
import coresearch.cvurl.io.exception.MappingException;
import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.multipart.MultipartBody;
import coresearch.cvurl.io.multipart.Part;
import coresearch.cvurl.io.utils.Resources;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;


public class CVurlRequestTest extends AbstractRequestTest {

    private static final String EMPTY_STRING = "";
    private static final String MULTIPART_BODY_TEST_JSON = "multipart-body-test.json";
    private static final String MULTIPART_HEADER_TEMPLATE = "multipart/%s;boundary=%s";
    public static final String BODY_AS_INPUT_STREAM_TXT = "body-as-input-stream-test.txt";
    private static String url = format(URL_PATTERN, PORT, TEST_ENDPOINT);

    @Test
    public void emptyResponseTest() {

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(EMPTY_STRING)));

        Response<String> response = cvurl.get(url).build().asString().orElseThrow(RuntimeException::new);

        assertEquals(EMPTY_STRING, response.getBody());
    }

    @Test
    public void asObjectOnUnparseableBodyShouldReturnEmptyOptional() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody("not a json string")));

        //when
        Optional<User> user = cvurl.get(url).build().asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    public void asObjectTest() throws JsonProcessingException {
        User user = ObjectGenerator.generateTestObject();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.get(url).build()
                .asObject(User.class, HttpStatus.OK)
                .orElseThrow(RuntimeException::new);

        assertEquals(user, resultUser);
    }

    @Test
    public void asyncAsStringTest() throws ExecutionException, InterruptedException {

        String body = "I am a string";
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(body)));

        Response<String> response = cvurl.get(url).build().asyncAsString()
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

        User resultUser = cvurl.get(url).build().asyncAsObject(User.class, HttpStatus.OK)
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
        Optional<Response<String>> response = cvurl.get(url).build().asString();

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
        Optional<Response<String>> response = cvurl.get(url).timeout(Duration.ofMillis(100)).build().asString();

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
        Optional<Response<String>> response = cvurl.get(url).timeout(Duration.ofMillis(100)).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void failedRequestTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Optional<Response<String>> response = cvurl.get(url).build().asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void differentResponseStatusCodeTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST)));

        //when
        Optional<User> user = cvurl.get(url).build().asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
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
        var response = cvurl.get(URI.create(urlWithParameters).toURL())
                .queryParam("param2", "2")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
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
        var response = cvurl.get(urlWithParameters)
                .queryParam("param2", "2")
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void responseWithStatusCode204AndNoContentLengthHeaderTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NO_CONTENT)));

        //when
        var response = cvurl.get(url).build().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.NO_CONTENT, response.status());
    }

    @Test
    public void queryParamsTest() {
        //given
        var queryParams = Map.of("param1", "val1", "param2", "val2");

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT + "?param1=val1&param2=val2"))
                .willReturn(WireMock.ok()));

        //when
        var response = cvurl.get(url).queryParams(queryParams).build().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());

    }

    @Test
    public void onSendErrorAsObjectShouldReturnEmptyOptionalTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Optional<User> user = cvurl.get(url).build().asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    public void sendWithSimpleMultipartBodyTest() {
        //given
        var plainTextPartName = "name1";
        var filePartName = "name2";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withMultipartRequestBody(aMultipart()
                        .withName(plainTextPartName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.TEXT_PLAIN))
                        .withBody(equalTo("content")))
                .withMultipartRequestBody(aMultipart()
                        .withName(filePartName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_JSON))
                        .withBody(equalTo("{}")))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.post(url)
                .body(MultipartBody.create()
                        .type(MultipartType.FORM)
                        .formPart(plainTextPartName, Part.of("content").contentType(MIMEType.TEXT_PLAIN))
                        .formPart(filePartName, Part.of("{}").contentType(MIMEType.APPLICATION_JSON)))
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendWithFileMultipartBody() throws IOException {
        //given
        Path jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var partName = "name1";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withMultipartRequestBody(aMultipart()
                        .withName(partName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo("application/json"))
                        .withBody(equalTo(Files.readString(jsonPath))))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.post(url)
                .body(MultipartBody.create()
                        .type(MultipartType.FORM)
                        .formPart(partName, Part.of(jsonPath)))
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void sendWithFileMultipartBodyCustomTypeShouldOverwriteAutodetectedTest() throws IOException {
        //given
        Path jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var partName = "name1";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withMultipartRequestBody(aMultipart()
                        .withName(partName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_XML))
                        .withBody(equalTo(Files.readString(jsonPath))))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.post(url)
                .body(MultipartBody.create()
                        .type(MultipartType.FORM)
                        .formPart(partName, Part.of(jsonPath).contentType(MIMEType.APPLICATION_XML)))
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @ParameterizedTest
    @ValueSource(strings = {MultipartType.FORM, MultipartType.MIXED,
            MultipartType.ALTERNATIVE, MultipartType.DIGEST, MultipartType.PARALLEL})
    public void settingMultipartBodyShouldGenerateProperHeader(String multipartType) {
        //given
        var boundary = "BOUNDARY";
        var contentType = format(MULTIPART_HEADER_TEMPLATE, multipartType, boundary);

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.CONTENT_TYPE, equalTo(contentType))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cvurl.post(url).body(MultipartBody
                .create(boundary)
                .type(multipartType))
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void bodyAsUrlEncodedFormDataTest() {
        //given
        var paramName1 = "paramName1";
        var paramName2 = "paramName2";
        var value1 = "value1";
        var value2 = "value2";
        var expectedBody = paramName1 + "=" + value1 + "&" + paramName2 + "=" + value2;
        var paramsMap = new LinkedHashMap<>() {{
            put(paramName1, value1);
            put(paramName2, value2);
        }};

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_FORM))
                .withRequestBody(equalTo(expectedBody))
                .willReturn(WireMock.aResponse()));

        //when
        var response = cvurl.post(url)
                .formData(paramsMap)
                .build()
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void bodyAsUrlEncodedFormDataWithEmptyMapTest() {
        assertThrows(IllegalStateException.class, () -> cvurl.post(url).formData(Map.of()));
    }

    @Test
    public void bodyAsInputStreamTest() throws IOException {
        //given
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        Response<InputStream> response = cvurl.post(url).build().asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertArrayEquals(Files.readAllBytes(Resources.get("__files/" + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().readAllBytes());

        response.getBody().close();
    }

    @Test
    public void responseBodyHandlingWithArbitraryBodyHandlerTest() throws IOException {
        //given
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        Response<Stream<String>> response = cvurl.post(url)
                .build()
                .as(HttpResponse.BodyHandlers.ofLines())
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertLinesMatch(Files.readAllLines(Resources.get("__files/" + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().collect(Collectors.toList()));
    }

    @Test
    public void asyncBodyAsInputStreamTest() throws IOException, ExecutionException, InterruptedException {
        //given
        boolean[] isThenApplyInvoked = {false};
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        Response<InputStream> response = cvurl.post(url).build().asyncAsStream()
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertArrayEquals(Files.readAllBytes(Resources.get("__files/" + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().readAllBytes());
        assertTrue(isThenApplyInvoked[0]);

        response.getBody().close();
    }

    @Test
    public void asyncResponseBodyHandlingWithArbitraryBodyHandlerTest() throws IOException, ExecutionException, InterruptedException {
        //given
        boolean[] isThenApplyInvoked = {false};
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        Response<Stream<String>> response = cvurl.post(url)
                .build()
                .asyncAs(HttpResponse.BodyHandlers.ofLines())
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertLinesMatch(Files.readAllLines(Resources.get("__files/" + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().collect(Collectors.toList()));
        assertTrue(isThenApplyInvoked[0]);
    }

    @Test
    public void gzipEncodedResponseBodyAsStringTest() throws IOException {
        //given
        var body = "Test body";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.ACCEPT_ENCODING, equalTo(HttpContentEncoding.GZIP))
                .willReturn(WireMock.aResponse()
                        .withBody(compressWithGZIP(body))
                        .withHeader(HttpHeader.CONTENT_ENCODING, HttpContentEncoding.GZIP)));

        //when
        var response = cvurl.post(url).acceptCompressed().build().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    public void gzipEncodedResponseBodyAsStreamTest() throws IOException {
        //given
        var body = "Test body";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.ACCEPT_ENCODING, equalTo(HttpContentEncoding.GZIP))
                .willReturn(WireMock.aResponse()
                        .withBody(compressWithGZIP(body))
                        .withHeader(HttpHeader.CONTENT_ENCODING, HttpContentEncoding.GZIP)));

        //when
        var response = cvurl.post(url).acceptCompressed().build().asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, new String(response.getBody().readAllBytes()));
    }

    @Test
    public void responseWithUnknownEncodingWithAcceptCompressedAsStringTest() throws IOException {
        //given
        var body = "Test body";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(body)
                        .withHeader(HttpHeaders.CONTENT_ENCODING, "unknown")));

        //when
        var response = cvurl.post(url).acceptCompressed().build().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    public void responseWithUnknownEncodingWithAcceptCompressedAsStreamTest() throws IOException {
        //given
        var body = "Test body";

        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withBody(body)
                        .withHeader(HttpHeaders.CONTENT_ENCODING, "unknown")));

        //when
        var response = cvurl.post(url).acceptCompressed().build().asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, new String(response.getBody().readAllBytes()));
    }

    @Test
    public void requestWithAcceptCompressedFaultTest() throws IOException {
        //given
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //then
        var response = cvurl.post(url).acceptCompressed().build().asStream();

        assertTrue(response.isEmpty());
    }

    private byte[] compressWithGZIP(String str) throws IOException {
        var out = new ByteArrayOutputStream();
        try (var gzipOutputStream = new GZIPOutputStream(out)) {
            gzipOutputStream.write(str.getBytes());
        }
        return out.toByteArray();
    }

}
