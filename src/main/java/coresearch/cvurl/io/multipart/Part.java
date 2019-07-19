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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class Part<T extends Part<T>> {
    private Map<String, String> headers;
    private byte[] content;

    protected Part(byte[] content) {
        this.headers = new HashMap<>();
        this.content = content;
    }

    public static Part of(byte[] content) {
        return new Part(content);
    }

    public static Part of(String content) {
        return new Part(content.getBytes());
    }

    public static PartWithFileContent of(Path filePath) {
        try {
            return new PartWithFileContent(filePath, Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new BadFileException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public T header(String key, String value) {
        this.headers.put(key.toLowerCase(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T headers(Map<String, String> headers) {
        this.headers.putAll(headers
                .entrySet()
                .stream()
                .collect(toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue)));

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T contentType(String mimeType) {
        header(HttpHeader.CONTENT_TYPE, mimeType);
        return (T) this;
    }

    boolean isContentTypeSet() {
        return this.headers.containsKey(HttpHeader.CONTENT_TYPE.toLowerCase());
    }

    public List<byte[]> asByteArrays(String boundary) {
        var result = new ArrayList<byte[]>();

        result.add(headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(joining("\r\n", "--" + boundary + "\r\n", "\r\n\r\n"))
                .getBytes(UTF_8));

        result.add(content);
        result.add("\r\n".getBytes(UTF_8));

        return result;
    }
}

