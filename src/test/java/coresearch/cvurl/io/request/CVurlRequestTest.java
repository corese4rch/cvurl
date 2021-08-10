package coresearch.cvurl.io.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.http.Fault;
import coresearch.cvurl.io.constant.*;
import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.ResponseMappingException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.helper.ObjectGenerator;
import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.multipart.MultipartBody;
import coresearch.cvurl.io.multipart.Part;
import coresearch.cvurl.io.utils.MockHttpClient;
import coresearch.cvurl.io.utils.Resources;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

class CVurlRequestTest extends AbstractRequestTest {

    private static final String EMPTY_STRING = "";
    private static final String MULTIPART_BODY_TEST_JSON = "multipart-body-test.json";
    private static final String MULTIPART_HEADER_TEMPLATE = "multipart/%s;boundary=%s";
    private static final String BODY_AS_INPUT_STREAM_TXT = "body-as-input-stream-test.txt";
    private static final String NOT_A_JSON_STRING = "not a json string";
    private static final String SECOND_PARAM = "param2";
    private static final String FIRST_NAME = "name1";
    private static final String FILE_PATH = "__files/";
    private static final String TEST_BODY = "Test body";
    private static final String URL = format(URL_PATTERN, PORT, TEST_ENDPOINT);

    @Test
    void shouldReturnEmptyStringInBodyWhenResponseContainsEmptyString() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(EMPTY_STRING)));

        //when
        var response = cVurl.get(URL).asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(EMPTY_STRING, response.getBody());
    }

    @Test
    void shouldReturnEmptyOptionalWhenStatusCodeIsOkAndResponseBodyIsInvalidJson() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(NOT_A_JSON_STRING)));

        //when
        var user = cVurl.get(URL).asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    void shouldReturnUserWhenStatusCodeOkAndResponseBodyIsValidJson() throws JsonProcessingException {
        //given
        var user = ObjectGenerator.generateTestObject();

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        //when
        var resultUser = cVurl.get(URL)
                .asObject(User.class, HttpStatus.OK)
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(user, resultUser);
    }

    @Test
    void shouldReturnExpectedBodyWhenExecutionModeIsAsync() throws ExecutionException, InterruptedException {
        //given
        var body = "I am a string";
        var isThenApplyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(body)));

        //when
        var response = cVurl.get(URL).asyncAsString()
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(body, response.getBody());
    }


    @Test
    void shouldReturnExpectedBodyWhenExecutionModeIsAsyncAndStatusCodeIsOk() throws JsonProcessingException,
            ExecutionException, InterruptedException {
        //given
        var user = ObjectGenerator.generateTestObject();
        var isThenApplyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        //when
        var resultUser = cVurl.get(URL).asyncAsObject(User.class, HttpStatus.OK)
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(user, resultUser);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGlobalTimeoutExpired() {
        //given
        var cVurl = new CVurl(CVurlConfig.builder()
                .requestTimeout(Duration.ofMillis(100))
                .build());

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        var response = cVurl.get(URL).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenRequestTimeoutExpired() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        var response = cVurl.get(URL).requestTimeout(Duration.ofMillis(100)).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGlobalTimeoutGreaterThanRequestTimeoutAndRequestTimeoutExpired() {
        //given
        var cVurl = new CVurl(CVurlConfig.builder()
                .requestTimeout(Duration.ofMillis(200))
                .build());

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withFixedDelay(200)
                        .withBody(EMPTY_STRING)));

        //when
        var response = cVurl.get(URL).requestTimeout(Duration.ofMillis(100)).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenRequestFailed() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().
                        withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        var response = cVurl.get(URL).asString();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenStatusCodeOkIsExpectedAndActualStatusCodeIsBadRequest() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST)));

        //when
        var user = cVurl.get(URL).asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    void shouldReturnStatusCodeOkWhenUrlContainsQueryParametersAndUrlIsInstanceOfURL() throws MalformedURLException {
        //given
        var params = "?params=1";
        var urlWithParameters = URL + params;

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT + params + "&param2=2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.get(URI.create(urlWithParameters).toURL())
                .queryParam(SECOND_PARAM, "2")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenUrlContainsQueryParametersAndUrlIsInstanceOfString() {
        //given
        var params = "?param1=1";
        var urlWithParameters = URL + params;

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT + params + "&param2=2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.get(urlWithParameters)
                .queryParam(SECOND_PARAM, "2")
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnStatusCodeNoContentWhenServerResponseStatusCodeIsNoContent() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().withStatus(HttpStatus.NO_CONTENT)));

        //when
        var response = cVurl.get(URL).asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.NO_CONTENT, response.status());
    }

    @Test
    void shouldReturnStatusCodeOkWhenQueryParametersArePassedAsMap() {
        //given
        var queryParams = Map.of("param1", "val1", SECOND_PARAM, "val2");

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT + "?param1=val1&param2=val2"))
                .willReturn(ok()));

        //when
        var response = cVurl.get(URL).queryParams(queryParams).asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldReturnEmptyOptionalWhenStatusCodeOkIsExpectedAndConnectionWasClosed() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        var user = cVurl.get(URL).asObject(User.class, HttpStatus.OK);

        //then
        assertTrue(user.isEmpty());
    }

    @Test
    void shouldSuccessfullySendMultipartBodyRequestWhenParametersAreValid() {
        //given
        var plainTextPartName = FIRST_NAME;
        var filePartName = "name2";

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
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
        var response = cVurl.post(URL)
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
    void shouldSuccessfullySendMultipartBodyRequestWhenContentStoredInFile() throws IOException {
        //given
        var jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var partName = FIRST_NAME;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withMultipartRequestBody(aMultipart()
                        .withName(partName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo("application/json"))
                        .withBody(equalTo(Files.readString(jsonPath))))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(URL)
                .body(MultipartBody.create()
                        .type(MultipartType.FORM)
                        .formPart(partName, Part.of(jsonPath)))
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldSuccessfullySendMultipartBodyRequestWhenContentTypeIsRedefined() throws IOException {
        //given
        var jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var partName = FIRST_NAME;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withMultipartRequestBody(aMultipart()
                        .withName(partName)
                        .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_XML))
                        .withBody(equalTo(Files.readString(jsonPath))))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(URL)
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
    void shouldGenerateProperHeaderWhenRequestTypeIsMultipart(String multipartType) {
        //given
        var boundary = "BOUNDARY";
        var contentType = format(MULTIPART_HEADER_TEMPLATE, multipartType, boundary);

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.CONTENT_TYPE, equalTo(contentType))
                .willReturn(aResponse().withStatus(HttpStatus.OK)));

        //when
        var response = cVurl.post(URL).body(MultipartBody
                .create(boundary)
                .type(multipartType))
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldSuccessfullySendFormDataRequestWhenParametersAreValid() {
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

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.CONTENT_TYPE, equalTo(MIMEType.APPLICATION_FORM))
                .withRequestBody(equalTo(expectedBody))
                .willReturn(aResponse()));

        //when
        var response = cVurl.post(URL)
                .formData(paramsMap)
                .asString()
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenFormDataIsEmptyMap() {
        //when
        Executable executable = () -> cVurl.post(URL).formData(Map.of());

        //then
        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void shouldReturnValidResponseWhenInputStreamIsExpected() throws IOException {
        //given
        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        var response = cVurl.post(URL).asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertArrayEquals(Files.readAllBytes(Resources.get(FILE_PATH + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().readAllBytes());

        response.getBody().close();
    }

    @Test
    void shouldReturnValidResponseWhenDefaultBodyHandlerIsUsed() throws IOException {
        //given
        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        var response = cVurl.post(URL)
                .as(HttpResponse.BodyHandlers.ofLines())
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertLinesMatch(Files.readAllLines(Resources.get(FILE_PATH + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().collect(Collectors.toList()));
    }

    @Test
    void shouldReturnValidResponseWhenInputStreamIsExpectedAndExecutionModeIsAsync() throws IOException,
            ExecutionException, InterruptedException {
        //given
        var isThenApplyInvoked = new boolean[]{false};
        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        var response = cVurl.post(URL).asyncAsStream()
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertArrayEquals(Files.readAllBytes(Resources.get(FILE_PATH + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().readAllBytes());
        assertTrue(isThenApplyInvoked[0]);

        response.getBody().close();
    }

    @Test
    void shouldReturnValidResponseWhenDefaultBodyHandlerIsUsedAndExecutionModeIsAsync() throws IOException,
            ExecutionException, InterruptedException {
        //given
        var isThenApplyInvoked = new boolean[]{false};
        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().withBodyFile(BODY_AS_INPUT_STREAM_TXT)));

        //when
        var response = cVurl.post(URL)
                .asyncAs(HttpResponse.BodyHandlers.ofLines())
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertLinesMatch(Files.readAllLines(Resources.get(FILE_PATH + BODY_AS_INPUT_STREAM_TXT)),
                response.getBody().collect(Collectors.toList()));
        assertTrue(isThenApplyInvoked[0]);
    }

    @Test
    void shouldReturnResponseBodyAsStringWhenBodyIsCompressedWithGZIP() throws IOException {
        //given
        var body = TEST_BODY;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.ACCEPT_ENCODING, equalTo(HttpContentEncoding.GZIP))
                .willReturn(aResponse()
                        .withBody(compressWithGZIP(body))
                        .withHeader(HttpHeader.CONTENT_ENCODING, HttpContentEncoding.GZIP)));

        //when
        var response = cVurl.post(URL).acceptCompressed().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    void shouldReturnResponseBodyAsStreamWhenBodyIsCompressedWithGZIP() throws IOException {
        //given
        var body = TEST_BODY;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .withHeader(HttpHeader.ACCEPT_ENCODING, equalTo(HttpContentEncoding.GZIP))
                .willReturn(aResponse()
                        .withBody(compressWithGZIP(body))
                        .withHeader(HttpHeader.CONTENT_ENCODING, HttpContentEncoding.GZIP)));

        //when
        var response = cVurl.post(URL).acceptCompressed().asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, new String(response.getBody().readAllBytes()));

        response.getBody().close();
    }

    @Test
    void shouldReturnResponseBodyAsStringWhenResponseWithUnknownEncoding() {
        //given
        var body = TEST_BODY;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(body)
                        .withHeader(HttpHeaders.CONTENT_ENCODING, "unknown")));

        //when
        var response = cVurl.post(URL).acceptCompressed().asString().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, response.getBody());
    }

    @Test
    void shouldReturnResponseBodyAsStreamWhenResponseWithUnknownEncoding() throws IOException {
        //given
        var body = TEST_BODY;

        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withBody(body)
                        .withHeader(HttpHeaders.CONTENT_ENCODING, "unknown")));

        //when
        var response = cVurl.post(URL).acceptCompressed().asStream().orElseThrow(RuntimeException::new);

        //then
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(body, new String(response.getBody().readAllBytes()));
    }

    @Test
    void shouldReturnEmptyOptionalWhenAcceptedCompressedAndRequestFailed() {
        //given
        wireMockServer.stubFor(post(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        var response = cVurl.post(URL).acceptCompressed().asStream();

        //then
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnBodyConvertedToUserWhenResponseContainsValidJson() throws JsonProcessingException {
        //given
        var user = ObjectGenerator.generateTestObject();

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        //when
        var resultUser = cVurl.get(URL).asObject(User.class);

        //then
        assertEquals(user, resultUser);
    }

    @Test
    void shouldThrowRequestExecutionExceptionWhenResponseBodyConvertedToUserIsExpected() {
        //given
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        //when
        Executable executable = () -> cVurl.get(URL).asObject(User.class);

        //then
        assertThrows(RequestExecutionException.class, executable);
    }

    @Test
    void shouldThrowResponseMappingExceptionWhenResponseBodyIsNotJson() {
        //given
        var body = NOT_A_JSON_STRING;
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //when
        Executable executable = () -> cVurl.get(URL).asObject(User.class);

        //then
        var responseMappingException = assertThrows(ResponseMappingException.class, executable);
        assertEquals(HttpStatus.OK, responseMappingException.getResponse().status());
        assertEquals(body, responseMappingException.getResponse().getBody());
    }

    @Test
    void shouldReturnResponseBodyConvertedToUserWhenExecutionModeIsAsync() throws JsonProcessingException,
            ExecutionException, InterruptedException {
        //given
        var user = ObjectGenerator.generateTestObject();
        var isThenApplyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(user))));

        //when
        var resultUser = cVurl.get(URL).asyncAsObject(User.class)
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(user, resultUser);
    }

    @Test
    void shouldThrowResponseMappingExceptionWhenResponseBodyIsNotJsonAndExecutionModeIsAsync() throws ExecutionException,
            InterruptedException {
        //given
        var body = "response body";
        var isExceptionallyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //when-then
        cVurl.get(URL).asyncAsObject(User.class)
                .exceptionally(exception -> {
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
    void shouldUseSameHttpRequestInstanceWhenExecutedMultipleTimes() {
        //given
        var httpClient = MockHttpClient.create();
        var request = new CVurl(httpClient).get(URL).create();

        //when
        request.asString();
        request.asString();

        //then
        var httpRequest1 = httpClient.getRequests().get(0);
        var httpRequest2 = httpClient.getRequests().get(1);

        assertSame(httpRequest1, httpRequest2);
    }

    @Test
    void shouldThrowUnexpectedResponseExceptionWhenReceiveUnexpectedResponseAndExecutionModeIsAsync()
            throws ExecutionException, InterruptedException {
        //given
        var isExceptionallyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NO_CONTENT)));

        //when-then
        cVurl.get(URL).asyncAsObject(User.class, HttpStatus.OK)
                .exceptionally(throwable -> {
                    isExceptionallyInvoked[0] = true;
                    assertTrue(throwable.getCause() instanceof UnexpectedResponseException);

                    UnexpectedResponseException exception = (UnexpectedResponseException) throwable.getCause();
                    assertEquals(HttpStatus.NO_CONTENT, exception.getResponse().status());

                    return null;
                })
                .get();

        assertTrue(isExceptionallyInvoked[0]);
    }

    @Test
    void shouldReturnListOfUsersWhenBodyTypeIsUsed() throws JsonProcessingException {
        //given
        var users = ObjectGenerator.generateListOfTestObjects();

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        var resultUsers = cVurl.get(URL).asObject(new BodyType<List<User>>() {});

        //then
        assertEquals(users, resultUsers);
    }

    @Test
    void shouldReturnListOfUsersWrappedInSetWhenBodyTypeIsUsed() throws JsonProcessingException {
        //given
        var users = Set.of(ObjectGenerator.generateListOfTestObjects());

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        var resultUsers = cVurl.get(URL).asObject(new BodyType<Set<List<User>>>() {});

        //then
        assertEquals(users, resultUsers);
    }

    @Test
    void shouldReturnListOfUsersWhenBodyTypeIsUsedAndStatusCodeOkIsExpected() throws JsonProcessingException {
        //given
        var users = ObjectGenerator.generateListOfTestObjects();

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        var resultUsers = cVurl.get(URL)
                .asObject(new BodyType<List<User>>() {}, HttpStatus.OK)
                .orElseThrow(RuntimeException::new);

        //then
        assertEquals(users, resultUsers);
    }

    @Test
    void shouldReturnListOfUsersWhenBodyTypeIsUsedAndExecutionModeIsAsync() throws JsonProcessingException,
            ExecutionException, InterruptedException {
        //given
        var users = ObjectGenerator.generateListOfTestObjects();
        var isThenApplyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        var resultUsers = cVurl.get(URL).asyncAsObject(new BodyType<List<User>>() {})
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(users, resultUsers);
    }

    @Test
    void shouldReturnListOfUsersWhenBodyTypeIsUsedAndStatusCodeOkIsExpectedAndExecutionModeIsAsync()
            throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        var users = ObjectGenerator.generateListOfTestObjects();
        var isThenApplyInvoked = new boolean[]{false};

        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(mapper.writeValueAsString(users))));

        //when
        var resultUsers = cVurl.get(URL).asyncAsObject(new BodyType<List<User>>() {}, HttpStatus.OK)
                .thenApply(res -> {
                    isThenApplyInvoked[0] = true;
                    return res;
                })
                .get();

        //then
        assertTrue(isThenApplyInvoked[0]);
        assertEquals(users, resultUsers);
    }

    @Test
    void shouldThrowResponseMappingExceptionWhenResponseBodyIsNotValidAndBodyTypeIsUsed() {
        //given
        var body = NOT_A_JSON_STRING;
        wireMockServer.stubFor(get(urlEqualTo(TEST_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(body)));

        //when
        Executable executable = () -> cVurl.get(URL).asObject(new BodyType<List<User>>() {});

        //then
        var responseMappingException = assertThrows(ResponseMappingException.class, executable);
        assertEquals(HttpStatus.OK, responseMappingException.getResponse().status());
        assertEquals(body, responseMappingException.getResponse().getBody());
    }

    @Test
    void shouldOverrideGlobalRequestConfigurationWhenRequestConfigurationIsProvided() throws NoSuchFieldException,
            IllegalAccessException {
        //given
        var timeout = Duration.ofSeconds(15);

        //when
        var requestConfiguration = getRequestConfiguration((CVurlRequest) cVurl.get(URL)
                .requestTimeout(timeout)
                .acceptCompressed(true)
                .logEnabled(true)
                .create());

        //then
        assertEquals(timeout, requestConfiguration.getRequestTimeout()
                .orElseThrow(() -> new IllegalStateException("No request timeout. It must be set.")));
        assertTrue(requestConfiguration.isAcceptCompressed());
        assertTrue(requestConfiguration.isLogEnabled());
    }

    private byte[] compressWithGZIP(String str) throws IOException {
        var out = new ByteArrayOutputStream();
        try (var gzipOutputStream = new GZIPOutputStream(out)) {
            gzipOutputStream.write(str.getBytes());
        }
        return out.toByteArray();
    }

    private RequestConfiguration getRequestConfiguration(CVurlRequest request) throws NoSuchFieldException,
            IllegalAccessException {
        var requestConfigurationField = request.getClass().getDeclaredField("requestConfiguration");
        requestConfigurationField.setAccessible(true);
        return (RequestConfiguration) requestConfigurationField.get(request);
    }
}
