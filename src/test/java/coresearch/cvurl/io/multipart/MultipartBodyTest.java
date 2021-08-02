package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.exception.MultipartFileFormException;
import coresearch.cvurl.io.utils.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultipartBodyTest {

    private static final String BOUNDARY = "BOUNDARY";
    private static final String MULTIPART_BODY_PART_TEMPLATE = "--%s\r\n%s\r\n%s\r\n";

    private static final String CONTENT_DISPOSITION_TEMPLATE = "form-data; name=\"%s\"";
    private static final String CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE = CONTENT_DISPOSITION_TEMPLATE + "; filename=\"%s\"";

    private static final String MULTIPART_BODY_TEST_JSON = "multipart-body-test.json";
    private static final String MULTIPART_BODY_TEST_UNKNOWN_EXTENSION = "multipart-body-test.ololo";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String CRLF = "\r\n";
    private static final String CONTENT = "content";

    @Test
    void shouldCreateValidMultipartBodyWhenContentTypeIsNotSpecified() {
        //given
        var partContent = CONTENT;
        var expectedResult = generateMultipartBody(new TestPart(partContent));

        //when
        var result = MultipartBody.create(BOUNDARY).part(Part.of(partContent));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenContentTypeIsSpecified() {
        //given
        var partContent = CONTENT;
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult =
                generateMultipartBody(new TestPart(partContent, Map.of(HttpHeader.CONTENT_TYPE, partContentType)));

        //when
        var result = MultipartBody.create(BOUNDARY).part(Part.of(partContent).contentType(partContentType));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenContentTypeIsTextPlain() {
        //given
        var partName = "name";
        var partContent = CONTENT;
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult =
                generateMultipartBody(new TestPart(partContent,
                        Map.of(HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName),
                                HttpHeader.CONTENT_TYPE, partContentType)));

        //when
        var result = MultipartBody.create(BOUNDARY)
                .formPart(partName, Part.of(partContent).contentType(partContentType));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenMultiplePartsArePresent() {
        //given
        var partName = "name";
        var partContent1 = "content1";
        var partContent2 = "content2";
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult =
                generateMultipartBody(new TestPart(partContent1,
                        Map.of(HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName),
                                HttpHeader.CONTENT_TYPE, partContentType)), new TestPart(partContent2));

        //when
        var result = MultipartBody.create(BOUNDARY)
                .formPart(partName, Part.of(partContent1).contentType(partContentType)).part(Part.of(partContent2));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenContentTypeIsJson() throws IOException {
        //given
        var partName = "name";
        var jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var expectedResult =
                generateMultipartBody(new TestPart(Files.readString(jsonPath), Map.of(HttpHeader.CONTENT_DISPOSITION,
                        format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE, partName, MULTIPART_BODY_TEST_JSON),
                        HttpHeader.CONTENT_TYPE, JSON_MIME_TYPE)));

        //when
        var multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(jsonPath));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenFilenameIsProvided() throws IOException {
        //given
        var partName = "name";
        var jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var filename = "filename";
        var expectedResult =
                generateMultipartBody(new TestPart(Files.readString(jsonPath), Map.of(HttpHeader.CONTENT_DISPOSITION,
                        format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE, partName, filename),
                        HttpHeader.CONTENT_TYPE, JSON_MIME_TYPE)));

        //when
        var multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(filename, jsonPath));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    void shouldCreateValidMultipartBodyFromFileWhenContentIsByteArray() throws IOException {
        //given
        var partName = "name";
        var jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var filename = "filename";
        var expectedResult =
                generateMultipartBody(new TestPart(Files.readString(jsonPath), Map.of(HttpHeader.CONTENT_DISPOSITION,
                        format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE, partName, filename),
                        HttpHeader.CONTENT_TYPE, JSON_MIME_TYPE)));

        //when
        var multipartBody = MultipartBody.create(BOUNDARY)
                .formPart(partName, Part.of(filename, JSON_MIME_TYPE, Files.readAllBytes(jsonPath)));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    void shouldCreateValidMultipartBodyWhenContentIsByteArray() {
        //given
        var partName = "name";
        var strContent = CONTENT;
        var content = strContent.getBytes();
        var expectedResult =
                generateMultipartBody(new TestPart(strContent,
                        Map.of(HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName))));

        //when
        var multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(content));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    void shouldThrowMultipartFileFormExceptionWhenIOExceptionIsOccurred() {
        //given
        var path = Paths.get("non-existing-file");

        //when
        Executable executable = () -> MultipartBody.create().formPart("name", Part.of(path));

        //then
        assertThrows(MultipartFileFormException.class, executable);
    }

    @Test
    void shouldCreateValidMultipartBodyFromFileWhenContentTypeIsNotSpecified() throws IOException {
        //given
        var partName = "name";
        var path = Resources.get(MULTIPART_BODY_TEST_UNKNOWN_EXTENSION);
        var expectedResult = generateMultipartBody(
                new TestPart(Files.readString(path), Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE,
                                partName, MULTIPART_BODY_TEST_UNKNOWN_EXTENSION))));

        //when
        var multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(path));

        //then no content type should be set
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    private String convertToString(MultipartBody multipartBody) {
        return multipartBody.asByteArrays().stream().map(String::new).collect(Collectors.joining(""));
    }

    //generates multipart body as defined by RFС1341
    private String generateMultipartBody(TestPart... testParts) {
        return Arrays.stream(testParts)
                .map(part -> format(MULTIPART_BODY_PART_TEMPLATE,
                        MultipartBodyTest.BOUNDARY,
                        part.headers.isEmpty() ? "" : part.headers.entrySet().stream()
                                .map(entry -> entry.getKey() + ":" + entry.getValue())
                                .collect(Collectors.joining(CRLF, "", CRLF)),
                        part.content))
                .collect(Collectors.joining("", "", "--" + MultipartBodyTest.BOUNDARY + "--"));
    }

    private static class TestPart {
        Map<String, String> headers;
        String content;

        public TestPart(String content) {
            this.content = content;
            this.headers = new HashMap<>();
        }

        public TestPart(String content, Map<String, String> headers) {
            this.content = content;
            this.headers = headers.entrySet()
                    .stream()
                    .collect(toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));
        }
    }

}