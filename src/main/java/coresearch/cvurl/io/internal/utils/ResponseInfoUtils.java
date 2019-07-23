package coresearch.cvurl.io.internal.utils;

import coresearch.cvurl.io.constant.HttpHeader;

import java.net.http.HttpResponse;
import java.util.Optional;

public class ResponseInfoUtils {
    public static Optional<String> getEncoding(HttpResponse.ResponseInfo responseInfo) {
        return responseInfo.headers().firstValue(HttpHeader.CONTENT_ENCODING);
    }
}
