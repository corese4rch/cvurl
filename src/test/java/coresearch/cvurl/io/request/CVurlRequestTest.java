package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import coresearch.cvurl.io.constant.*;
import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.ResponseMappingException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.mapper.CVType;
import coresearch.cvurl.io.model.Configuration;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.multipart.MultipartBody;
import coresearch.cvurl.io.multipart.Part;
import coresearch.cvurl.io.utils.Resources;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;


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

        Response<String> response = cvurl.get(url).asString().orElseThrow(RuntimeException::new);

        assertEquals(EMPTY_STRING, response.getBody());
    }

    @Test
    public void asObjectWithStatusCodeOnUnparseableBodyShouldReturnEmptyOptional() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody("not a json string")));

        //when
        Optional<User> user = cvurl.get(url).asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    public void asObjectWithStatusCodeTest() throws JsonProcessingException {
        User user = ObjectGenerator.generateTestObject();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.get(url)
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

        Response<String> response = cvurl.get(url).asyncAsString()
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
    public void asyncAsObjectWithStatusCodeTest() throws JsonProcessingException, ExecutionException, InterruptedException {

        User user = ObjectGenerator.generateTestObject();
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.get(url).asyncAsObject(User.class, HttpStatus.OK)
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
        Optional<Response<String>> response = cvurl.get(url).asString();

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
        Optional<Response<String>> response = cvurl.get(url).timeout(Duration.ofMillis(100)).asString();

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
        Optional<Response<String>> response = cvurl.get(url).timeout(Duration.ofMillis(100)).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void failedRequestTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Optional<Response<String>> response = cvurl.get(url).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    public void differentResponseStatusCodeTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST)));

        //when
        Optional<User> user = cvurl.get(url).asObject(User.class, HttpStatus.OK);

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
        var response = cvurl.get(url).asString().orElseThrow(RuntimeException::new);

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
        var response = cvurl.get(url).queryParams(queryParams).asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    public void onSendErrorAsObjectWithStatusCodeShouldReturnEmptyOptionalTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Optional<User> user = cvurl.get(url).asObject(User.class, HttpStatus.OK);

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
        Map<String, String> paramsMap = new LinkedHashMap<>() {{
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
        Response<InputStream> response = cvurl.post(url).asStream().orElseThrow(RuntimeException::new);

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
        Response<InputStream> response = cvurl.post(url).asyncAsStream()
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
        var response = cvurl.post(url).acceptCompressed().asString().orElseThrow(RuntimeException::new);

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
        var response = cvurl.post(url).acceptCompressed().asStream().orElseThrow(RuntimeException::new);

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
        var response = cvurl.post(url).acceptCompressed().asString().orElseThrow(RuntimeException::new);

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
        var response = cvurl.post(url).acceptCompressed().asStream().orElseThrow(RuntimeException::new);

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
        var response = cvurl.post(url).acceptCompressed().asStream();

        assertTrue(response.isEmpty());
    }

    @Test
    public void asObjectTest() throws JsonProcessingException {
        User user = ObjectGenerator.generateTestObject();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        User resultUser = cvurl.get(url)
                .asObject(User.class);

        assertEquals(user, resultUser);
    }

    @Test
    public void onSendErrorAsObjectShouldThrowRequestExecutionExceptionTest() {
        //given
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //then
        assertThrows(RequestExecutionException.class, () -> cvurl.get(url).asObject(User.class));
    }

    @Test
    public void asObjectOnUnparseableBodyShouldThrowResponseMappingException() {
        //given
        var body = "not a json string";
        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //when
        ResponseMappingException responseMappingException = assertThrows(ResponseMappingException.class,
                () -> cvurl.get(url).asObject(User.class));

        //then
        assertEquals(HttpStatus.OK, responseMappingException.getResponse().status());
        assertEquals(body, responseMappingException.getResponse().getBody());
    }

    @Test
    public void asyncAsObjectTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        User user = ObjectGenerator.generateTestObject();
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        //when
        User resultUser = cvurl.get(url).asyncAsObject(User.class)
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(user, resultUser);
    }

    @Test
    public void asyncAsObjectWithUnparseableBodyTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        var body = "response body";
        boolean[] isExceptionallyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //then
        cvurl.get(url).asyncAsObject(User.class)
                .exceptionally(exception ->
                {
                    assertTrue(exception.getCause() instanceof ResponseMappingException);
                    var responseMappingException = ((ResponseMappingException) exception.getCause());
                    assertEquals(body, responseMappingException.getResponse().getBody());
                    assertEquals(HttpStatus.OK, responseMappingException.getResponse().status());

                    isExceptionallyInvoked[0] = true;
                    return null;
                })
                .get();

        assertTrue(isExceptionallyInvoked[0]);
    }

    @Test
    public void reusableRequestCreationTest() throws IOException, InterruptedException {
        //given
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Request request = new CVurl(httpClient).get(url).create();

        ArgumentCaptor<HttpRequest> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        //when
        request.asString();
        request.asString();

        //then both executions are done on the same request
        Mockito.verify(httpClient, times(2)).send(
                httpRequestArgumentCaptor.capture(), Mockito.any(HttpResponse.BodyHandler.class));

        HttpRequest httpRequest1 = httpRequestArgumentCaptor.getAllValues().get(0);
        HttpRequest httpRequest2 = httpRequestArgumentCaptor.getAllValues().get(1);

        assertSame(httpRequest1, httpRequest2);
    }

    @Test
    public void unexpectedResponseTest() throws ExecutionException, InterruptedException {
        //given
        boolean[] isExceptionallyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NO_CONTENT)));

        //when
        cvurl.get(url).asyncAsObject(User.class, HttpStatus.OK)
                .exceptionally(throwable -> {
                    isExceptionallyInvoked[0] = true;
                    assertTrue(throwable.getCause() instanceof UnexpectedResponseException);

                    UnexpectedResponseException exception = (UnexpectedResponseException) throwable.getCause();
                    assertEquals(HttpStatus.NO_CONTENT, exception.getResponse().status());

                    return null;
                })
                .get();

        //then
        assertTrue(isExceptionallyInvoked[0]);
    }

    @Test
    public void asObjectWithCVTypeTest() throws JsonProcessingException {
        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        List<User> resultUsers = cvurl.get(url).asObject(new CVType<>() {});

        //then
        assertEquals(users, resultUsers);
    }

    @Test
    public void asObjectWithCVTypeWithNestedGenericsTest() throws JsonProcessingException {
        //given
        Set<List<User>> users = Set.of(ObjectGenerator.generateListOfTestObjects());

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        Set<List<User>> resultUsers = cvurl.get(url).asObject(new CVType<>() {});

        //then
        assertEquals(users, resultUsers);
    }

    @Test
    public void asObjectWithStatusCodeWithCVTypeTest() throws JsonProcessingException {
        List<User> users = ObjectGenerator.generateListOfTestObjects();

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        List<User> resultUsers = cvurl.get(url)
                .asObject(new CVType<List<User>>() {}, HttpStatus.OK)
                .orElseThrow(RuntimeException::new);

        assertEquals(users, resultUsers);
    }

    @Test
    public void asyncAsObjectWithCVTypeTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        List<User> resultUsers = cvurl.get(url).asyncAsObject(new CVType<List<User>>() {})
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(users, resultUsers);
    }

    @Test
    public void asyncAsObjectWithStatusCodeWithCVTypeTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        List<User> users = ObjectGenerator.generateListOfTestObjects();
        boolean[] isThenApplyInvoked = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        List<User> resultUsers = cvurl.get(url).asyncAsObject(new CVType<List<User>>() {}, HttpStatus.OK)
                .thenApply(res ->
                {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        assertTrue(isThenApplyInvoked[0]);
        assertEquals(users, resultUsers);
    }

    private byte[] compressWithGZIP(String str) throws IOException {
        var out = new ByteArrayOutputStream();
        try (var gzipOutputStream = new GZIPOutputStream(out)) {
            gzipOutputStream.write(str.getBytes());
        }
        return out.toByteArray();
    }
}
