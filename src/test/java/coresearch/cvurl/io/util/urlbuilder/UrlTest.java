package coresearch.cvurl.io.util.urlbuilder;

import coresearch.cvurl.io.exception.BadUrlException;
import coresearch.cvurl.io.util.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UrlTest {

    private static final String VALIDATION_ERROR_MESSAGE = "%s parameter cannot be null";
    private static final String URL = "http://www.google.com";

    @ParameterizedTest
    @ValueSource(strings = {
            "http://www.google.com/",
            "http://www.google.com// ",
            "http://www.google.com///"
    })
    void shouldReturnValidUrlWhenProvidedUrlIsValid(String url) {
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
    void shouldReturnValidUrlWhenSchemaAndHostAreValid(String schema, String host) {
        //given
        var expectedResult = "http://www.google.com/";

        //when
        var resultUrl = Url.of(schema, host).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"path/", " /path/", " //path// "})
    void shouldReturnValidUrlWhenUrlAndPathAreValid(String path) {
        //given
        var url = URL;
        var expectedResult = url + "/path/";

        //when
        var resultUrl = Url.of(url).path(path).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @Test
    void shouldReturnValidUrlWhenPathContainsSlashes() {
        //given
        var url = URL;
        var path = "path1/path2";
        var expectedUrl = url + "/" + path;

        //when
        var resultUrl = Url.of(url).path(path).create().toString();

        //then
        assertEquals(expectedUrl, resultUrl);
    }

    @Test
    void shouldThrowBadUrlExceptionWhenProtocolIsInvalid() {
        //given
        var badUrl = "shttp://www.google.com";

        //when
        Executable executable = () -> Url.of(badUrl).create();

        //then
        assertThrows(BadUrlException.class, executable);
    }

    @Test
    void shouldReturnNewInstanceWhenAdditionalPathIsAdded() {
        //given
        var urlStr = URL;
        var path = "path";
        var url = Url.of(urlStr);

        //when
        var urlWithPath = url.path(path);

        //then
        assertNotSame(url, urlWithPath);
        assertEquals(url.create().toString(), urlStr);
        assertEquals(urlStr + "/" + path, urlWithPath.create().toString());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUrlValueIsNull() {
        //when
        Executable executable = () -> Url.of(null);

        //then
        var nullPointerException = assertThrows(NullPointerException.class, executable);
        assertEquals(getValidationErrorMessage("url"), nullPointerException.getMessage());

    }

    @Test
    void shouldThrowNullPointerExceptionWhenSchemaIsNull() {
        //when
        Executable executable = () -> Url.of(null, "");

        //then
        var nullPointerException = assertThrows(NullPointerException.class, executable);
        assertEquals(getValidationErrorMessage("schema"), nullPointerException.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenHostIsNull() {
        //when
        Executable executable = () -> Url.of("", null);

        //then
        var nullPointerException = assertThrows(NullPointerException.class, executable);
        assertEquals(getValidationErrorMessage("host"), nullPointerException.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenPathIsNull() {
        //when
        Executable executable = () -> Url.of("url").path(null);

        //then
        var nullPointerException = assertThrows(NullPointerException.class, executable);
        assertEquals(getValidationErrorMessage("path"), nullPointerException.getMessage());
    }

    private String getValidationErrorMessage(String paramName) {
        return String.format(VALIDATION_ERROR_MESSAGE, paramName);
    }
}