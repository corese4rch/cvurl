package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpContentEncoding;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.exception.ResponseBodyHandlingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static coresearch.cvurl.io.internal.utils.ResponseInfoUtils.getEncoding;

public class CompressedStringBodyHandler implements HttpResponse.BodyHandler<String> {

    CompressedStringBodyHandler() {
    }

    @Override
    public HttpResponse.BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
        Optional<String> encoding = responseInfo.headers().firstValue(HttpHeader.CONTENT_ENCODING);

        if (encoding.isPresent() && encoding.get().equals(HttpContentEncoding.GZIP)) {
            return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), this::decompressGZIP);
        }

        return HttpResponse.BodyHandlers.ofString().apply(responseInfo);
    }

    private String decompressGZIP(byte[] bytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes)); var autoCloseOutputStream = outputStream) {
            gzipInputStream.transferTo(autoCloseOutputStream);
        } catch (IOException e) {
            throw new ResponseBodyHandlingException(e.getMessage(), e);
        }

        return new String(outputStream.toByteArray());
    }
}
