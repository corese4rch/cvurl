package coresearch.cvurl.io.request.handler;

import coresearch.cvurl.io.constant.HttpContentEncoding;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.exception.ResponseBodyHandlingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class CompressedStringBodyHandler implements HttpResponse.BodyHandler<String> {

    @Override
    public HttpResponse.BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
        Optional<String> encoding = responseInfo.headers().firstValue(HttpHeader.CONTENT_ENCODING);

        if (encoding.isPresent() && encoding.get().equals(HttpContentEncoding.GZIP)) {
            return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), this::decompressGZIP);
        }

        return HttpResponse.BodyHandlers.ofString().apply(responseInfo);
    }

    private String decompressGZIP(byte[] bytes) {
        try (var gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
             var outputStream = new ByteArrayOutputStream()) {

            gzipInputStream.transferTo(outputStream);
            return new String(outputStream.toByteArray());

        } catch (IOException e) {
            throw new ResponseBodyHandlingException(e.getMessage(), e);
        }

    }
}
