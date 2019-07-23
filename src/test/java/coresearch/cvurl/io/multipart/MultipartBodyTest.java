package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.utils.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipartBodyTest {

    private static final String BOUNDARY = "BOUNDARY";
    private static final String MULTIPART_BODY_PART_TEMPLATE = "--%s\r\n%s\r\n%s\r\n";

    private static final String CONTENT_DISPOSITION_TEMPLATE = "form-data; name=\"%s\"";
    private static final String CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE = CONTENT_DISPOSITION_TEMPLATE + "; filename=\"%s\"";

    private static final String MULTIPART_BODY_TEST_JSON = "multipart-body-test.json";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String CRLF = "\r\n";

    @Test
    public void singlePartWithoutContentTypeTest() throws IOException {
        //given
        var partContent = "content";
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(partContent));

        //when
        MultipartBody result = MultipartBody.create(BOUNDARY).part(Part.of(partContent));

        //then
        assertEquals(expectedResult, convertToString(result));
    }


    @Test
    public void singlePartWithContentTypeTest() throws IOException {
        //given
        var partContent = "content";
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(partContent, Map.of(
                        HttpHeader.CONTENT_TYPE, partContentType)));

        //when
        MultipartBody result = MultipartBody.create(BOUNDARY).part(Part.of(partContent).contentType(partContentType));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    public void singleFormPartTest() {
        //given
        var partName = "name";
        var partContent = "content";
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(partContent, Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName),
                        HttpHeader.CONTENT_TYPE, partContentType)));

        //when
        MultipartBody result = MultipartBody.create(BOUNDARY)
                .formPart(partName, Part.of(partContent).contentType(partContentType));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    public void mixedPartsTest() {
        //given
        var partName = "name";
        var partContent1 = "content1";
        var partContent2 = "content2";
        var partContentType = MIMEType.TEXT_PLAIN;
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(partContent1, Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName),
                        HttpHeader.CONTENT_TYPE, partContentType)),
                new TestPart(partContent2));

        //when
        MultipartBody result = MultipartBody.create(BOUNDARY)
                .formPart(partName, Part.of(partContent1).contentType(partContentType))
                .part(Part.of(partContent2));

        //then
        assertEquals(expectedResult, convertToString(result));
    }

    @Test
    public void fileFormPartTest() throws IOException {
        //given
        var partName = "name";
        Path jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(Files.readString(jsonPath), Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE,
                                partName, MULTIPART_BODY_TEST_JSON),
                        HttpHeader.CONTENT_TYPE, JSON_MIME_TYPE)));

        //when
        MultipartBody multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(jsonPath));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    public void fileFormPartWithCustomFilenameTest() throws IOException {
        //given
        var partName = "name";
        Path jsonPath = Resources.get(MULTIPART_BODY_TEST_JSON);
        var filename = "filename";
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(Files.readString(jsonPath), Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE,
                                partName, filename),
                        HttpHeader.CONTENT_TYPE, JSON_MIME_TYPE)));

        //when
        MultipartBody multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, filename, Part.of(jsonPath));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }

    @Test
    public void byteArrayPartTest() throws IOException {
        //given
        var partName = "name";
        var strContent = "content";
        var content = strContent.getBytes();
        var expectedResult = generateMultipartBody(BOUNDARY,
                new TestPart(strContent, Map.of(
                        HttpHeader.CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_TEMPLATE, partName))));

        //when
        MultipartBody multipartBody = MultipartBody.create(BOUNDARY).formPart(partName, Part.of(content));

        //then
        assertEquals(expectedResult, convertToString(multipartBody));
    }


    private String convertToString(MultipartBody multipartBody) {
        return multipartBody.asByteArrays().stream().map(String::new).collect(Collectors.joining(""));
    }

    //generates multipart body as defined by RFС1341
    private String generateMultipartBody(String boundary, TestPart... testParts) {
        return Arrays.stream(testParts)
                .map(part -> format(MULTIPART_BODY_PART_TEMPLATE,
                        boundary,
                        part.headers.isEmpty() ? "" : part.headers.entrySet().stream()
                                .map(entry -> entry.getKey() + ":" + entry.getValue())
                                .collect(Collectors.joining(CRLF, "", CRLF)),
                        part.content))
                .collect(Collectors.joining("", "", "--" + boundary + "--"));
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