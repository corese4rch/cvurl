package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.exception.BadFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static coresearch.cvurl.io.util.Validation.notNullParam;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class Part<T extends Part<T>> {
    public static final String CRLF = "\r\n";
    public static final String BOUNDARY_DELIMITER = "--";
    private Map<String, String> headers;
    private byte[] content;

    protected Part(byte[] content) {
        this.headers = new HashMap<>();
        this.content = content;
    }

    public static Part of(byte[] content) {
        notNullParam(content, "content");

        return new Part(content);
    }

    public static Part of(String content) {
        notNullParam(content, "content");

        return new Part(content.getBytes());
    }

    public static PartWithFileContent of(Path filePath) {
        notNullParam(filePath, "filePath");

        try {
            return new PartWithFileContent(filePath, Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new BadFileException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public T header(String key, String value) {
        notNullParam(key, "key");
        notNullParam(value, "value");

        this.headers.put(key.toLowerCase(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T headers(Map<String, String> headers) {
        notNullParam(headers, "headers");

        this.headers.putAll(headers
                .entrySet()
                .stream()
                .collect(toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue)));

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T contentType(String mimeType) {
        notNullParam(mimeType, "mimeType");

        header(HttpHeader.CONTENT_TYPE, mimeType);
        return (T) this;
    }

    boolean isContentTypeSet() {
        return this.headers.containsKey(HttpHeader.CONTENT_TYPE.toLowerCase());
    }

    List<byte[]> asByteArrays(String boundary) {
        var result = new ArrayList<byte[]>();

        result.add((BOUNDARY_DELIMITER + boundary + CRLF).getBytes());

        if (!headers.isEmpty()) {
            result.add(headers.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(joining(CRLF, "", CRLF))
                    .getBytes(UTF_8));
        }

        result.add(CRLF.getBytes());
        result.add(content);
        result.add(CRLF.getBytes(UTF_8));

        return result;
    }
}

