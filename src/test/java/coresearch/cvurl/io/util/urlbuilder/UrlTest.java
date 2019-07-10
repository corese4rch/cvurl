package coresearch.cvurl.io.util.urlbuilder;

import coresearch.cvurl.io.exception.BadUrlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class UrlTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "http://www.google.com",
            "http://www.google.com/",
            "/http://www.google.com// "
    })
    public void ofBasicUrlTest(String url) {
        //given
        var expectedResult = "http://www.google.com";

        //when
        var resultUrl = Url.of(url).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "http www.google.com",
            "http /www.google.com/",
            "http:// /www.google.com/",
            "http::/// /www.google.com// "
    }, delimiter = ' ')
    public void ofSchemaAndHostTest(String schema, String host) {
        //given
        var expectedResult = "http://www.google.com";

        //when
        var resultUrl = Url.of(schema, host).create().toString();

        //then
        assertEquals(expectedResult, resultUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"path", "/path", "/path/", " //path// "})
    public void pathTest(String path) {
        //given
        var url = "http://www.google.com";
        var expectedResult = url + "/path";

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
}