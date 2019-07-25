package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpContentEncoding;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.exception.ResponseBodyHandlingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class CompressedInputStreamBodyHandler implements HttpResponse.BodyHandler<InputStream> {

    @Override
    public HttpResponse.BodySubscriber<InputStream> apply(HttpResponse.ResponseInfo responseInfo) {
        Optional<String> encoding = responseInfo.headers().firstValue(HttpHeader.CONTENT_ENCODING);

        if (encoding.isPresent() && encoding.get().equals(HttpContentEncoding.GZIP)) {
            return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), this::getGZIPInputStream);
        }

        return HttpResponse.BodySubscribers.ofInputStream();
    }

    private InputStream getGZIPInputStream(byte[] bytes) {
        try {
            return new GZIPInputStream(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new ResponseBodyHandlingException(e.getMessage(), e);
        }
    }
}
