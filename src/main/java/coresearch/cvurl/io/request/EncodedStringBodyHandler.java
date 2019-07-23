package coresearch.cvurl.io.request;

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

public class EncodedStringBodyHandler implements HttpResponse.BodyHandler<String> {

    EncodedStringBodyHandler() {
    }

    @Override
    public HttpResponse.BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
        Optional<String> encoding = getEncoding(responseInfo);
        if (encoding.isEmpty()) {
            return HttpResponse.BodyHandlers.ofString().apply(responseInfo);
        }

        if (encoding.get().equals("gzip")) {
            return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), this::decompressGZIP);
        }

        throw new ResponseBodyHandlingException("Unknown content encoding: " + encoding);
    }

    private String decompressGZIP(byte[] bytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (InputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes)); var autoCloseOutputStream = outputStream) {
            gzipInputStream.transferTo(autoCloseOutputStream);
        } catch (IOException e) {
            throw new ResponseBodyHandlingException(e.getMessage(), e);
        }

        //TODO: add response body character encoding detection
        return new String(outputStream.toByteArray());
    }
}
