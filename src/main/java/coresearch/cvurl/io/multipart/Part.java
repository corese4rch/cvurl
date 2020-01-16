package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.exception.MultipartFileFormException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * Represent part of multipart data.
 */
public class Part<T extends Part<T>> {

    private static final String CRLF = "\r\n";
    private static final String BOUNDARY_DELIMITER = "--";
    private static final String CONTENT_ARGUMENT = "content";
    private Map<String, String> headers;
    private byte[] content;

    protected Part(byte[] content) {
        this.headers = new HashMap<>();
        this.content = content;
    }

    /**
     * Creates new instance of {@link Part}
     *
     * @param content content part
     * @return this {@link Part}
     */
    public static Part of(byte[] content) {
        notNullParam(content, CONTENT_ARGUMENT);

        return new Part(content);
    }

    /**
     * Creates new instance of {@link Part}
     *
     * @param content content part
     * @return this {@link Part}
     */
    public static Part of(String content) {
        notNullParam(content, CONTENT_ARGUMENT);

        return new Part(content.getBytes());
    }

    /**
     * Creates new instance of {@link Part} using file from provided filePath
     * Throws {@link MultipartFileFormException} in case {@link IOException} happens
     * while reading from the file. If file content type can be autodetected then it will
     * be set as part header, otherwise part won't have content type header.
     *
     * @param filePath path to file that will be used as content.
     * @return this {@link Part}
     */
    public static PartWithFileContent of(Path filePath) {
        return of(filePath.getFileName().toString(), filePath);
    }

    /**
     * Creates new instance of {@link Part} using file from provided filePath and filename
     * Throws {@link MultipartFileFormException} in case {@link IOException} happens
     * while reading from the file. If file content type can be autodetected then it will
     * be set as part header, otherwise part won't have content type header.
     *
     * @param filePath path to file that will be used as content.
     * @return this {@link Part}
     */
    public static PartWithFileContent of(String fileName, Path filePath) {
        notNullParam(fileName, "fileName");
        notNullParam(filePath, "filePath");

        try {
            PartWithFileContent part = new PartWithFileContent(fileName, Files.readAllBytes(filePath));
            Optional.ofNullable(Files.probeContentType(filePath))
                    .ifPresent(part::contentType);
            return part;
        } catch (IOException e) {
            throw new MultipartFileFormException(e.getMessage(), e);
        }
    }

    /**
     * Creates new instance of {@link Part} using provided fileName, content and content type.
     *
     * @return this {@link Part}
     */
    public static PartWithFileContent of(String fileName, String contentType, byte[] content) {
        notNullParam(fileName, "filePath");
        notNullParam(contentType, "contentType");
        notNullParam(content, CONTENT_ARGUMENT);

        return new PartWithFileContent(fileName, content).contentType(contentType);
    }

    /**
     * Add a header to the part.
     *
     * @param name  header name
     * @param value header value
     * @return this {@link Part}
     */
    @SuppressWarnings("unchecked")
    public T header(String name, String value) {
        notNullParam(name, "key");
        notNullParam(value, "value");

        this.headers.put(name.toLowerCase(), value);
        return (T) this;
    }

    /**
     * Add headers to the part.
     *
     * @param headers headers to add
     * @return this {@link Part}
     */
    @SuppressWarnings("unchecked")
    public T headers(Map<String, String> headers) {
        notNullParam(headers, "headers");

        this.headers.putAll(headers
                .entrySet()
                .stream()
                .collect(toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue)));

        return (T) this;
    }

    /**
     * Set a content-type header of the part.
     *
     * @param mimeType value to be set as content-type header.
     * @return this {@link Part}
     */
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

