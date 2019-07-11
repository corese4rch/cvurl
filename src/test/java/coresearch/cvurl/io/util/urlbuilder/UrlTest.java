package coresearch.cvurl.io.util.urlbuilder;

import coresearch.cvurl.io.exception.BadUrlException;
import coresearch.cvurl.io.util.Url;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UrlTest {

    private static final String VALIDATION_ERROR_MESSAGE = "%s parameter cannot be null";

    @ParameterizedTest
    @ValueSource(strings = {
            "http://www.google.com/",
            "http://www.google.com// "
    })
    public void ofBasicUrlTest(String url) {
        //given
        var expectedResult = "http://www.google.com/";

        //when
        var resultUrl = Url.of(url).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "http www.google.com/",
            "http /www.google.com/",
            "http /www.google.com// "
    }, delimiter = ' ')
    public void ofSchemaAndHostTest(String schema, String host) {
        //given
        var expectedResult = "http://www.google.com/";

        //when
        var resultUrl = Url.of(schema, host).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"path/", " /path/", " //path// "})
    public void pathTest(String path) {
        //given
        var url = "http://www.google.com";
        var expectedResult = url + "/path/";

        //when
        var resultUrl = Url.of(url).path(path).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @Test
    public void nestedPathTest() {
        //given
        var url = "http://www.google.com";
        var path = "path1/path2";
        var expectedUrl = url + "/" + path;

        //when
        var resultUrl = Url.of(url).path(path).create().toString();

        //then
        assertEquals(expectedUrl, resultUrl);
    }

    @Test
    public void createThrowsBadUrlExceptionTest() {
        //given
        var badUrl = "shttp://www.google.com";

        //then
        Assertions.assertThrows(BadUrlException.class, () -> Url.of(badUrl).create());
    }

    @Test
    public void pathReturnsNewObjectTest() {
        //given
        var urlStr = "http://www.google.com";
        var path = "path";
        var url = Url.of(urlStr);

        //when
        var urlWithPath = url.path(path);

        //then
        assertNotSame(url, urlWithPath);
        assertEquals(url.create().toString(), urlStr);
        assertEquals(urlWithPath.create().toString(), urlStr + "/" + path);
    }

    @Test
    public void ofWithNullUrlShouldThrowNPEWithMessage() {
        //when
        var nullPointerException = assertThrows(NullPointerException.class, () -> Url.of(null));

        //then
        assertEquals(nullPointerException.getMessage(), getValidationErrorMessage("url"));
    }

    @Test
    public void ofWithNullSchemaShouldThrowNPEWithMessage() {
        //when
        var nullPointerException = assertThrows(NullPointerException.class, () -> Url.of(null, ""));

        //then
        assertEquals(nullPointerException.getMessage(), getValidationErrorMessage("schema"));
    }

    @Test
    public void ofWithNullHostShouldThrowNPEWithMessage() {
        //when
        var nullPointerException = assertThrows(NullPointerException.class, () -> Url.of("", null));

        //then
        assertEquals(nullPointerException.getMessage(), getValidationErrorMessage("host"));
    }

    @Test
    public void pathWithNullPathShouldThrowNPEWithMessage() {
        //when
        var nullPointerException = assertThrows(NullPointerException.class, () -> Url.of("url").path(null));

        //then
        assertEquals(nullPointerException.getMessage(), getValidationErrorMessage("path"));
    }

    private String getValidationErrorMessage(String paramName) {
        return String.format(VALIDATION_ERROR_MESSAGE, paramName);
    }
}