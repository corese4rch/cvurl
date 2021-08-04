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
 * Represent a part of multipart data.
 *
 * @since 1.0
 */
public class Part<T extends Part<T>> {

    private static final String CRLF = "\r\n";
    private static final String BOUNDARY_DELIMITER = "--";
    private static final String CONTENT_ARGUMENT = "content";
    private final Map<String, String> headers;
    private final byte[] content;

    protected Part(byte[] content) {
        this.headers = new HashMap<>();
        this.content = content;
    }

    /**
     * Creates a new instance of the {@link Part} class
     *
     * @param content - the content in bytes
     * @return an instance of the {@link Part} class
     */
    public static Part of(byte[] content) {
        notNullParam(content, CONTENT_ARGUMENT);

        return new Part(content);
    }

    /**
     * Creates a new instance of the {@link Part} class
     *
     * @param content - the content as string
     * @return an instance of the {@link Part} class
     */
    public static Part of(String content) {
        notNullParam(content, CONTENT_ARGUMENT);

        return new Part(content.getBytes());
    }

    /**
     * Creates a new instance of the {@link Part} class using the file at the specified path.
     * If the content type of the file can be detected automatically, it will be added to headers.
     * Otherwise, the part will not have the Content-Type header.
     *
     * @param filePath - the path to the file to be used as content
     * @return an instance of the {@link Part} class
     * @throws MultipartFileFormException in case {@link IOException} happens while reading from the file.
     */
    public static PartWithFileContent of(Path filePath) {
        return of(filePath.getFileName().toString(), filePath);
    }

    /**
     * Creates a new instance of the {@link Part} class using the file at the specified path.
     * If the content type of the file can be detected automatically, it will be added to headers.
     * Otherwise, the part will not have the Content-Type header.
     *
     * @param fileName - the name of the file
     * @param filePath - the path to the file to be used as content
     * @return an instance of the {@link Part} class
     * @throws MultipartFileFormException in case {@link IOException} happens while reading from the file.
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
     * Creates a new instance of the {@link Part} class using the specified file name, content type, and content in bytes.
     *
     * @param fileName - the name of the file
     * @param contentType - the content type of the file
     * @param content - the content in bytes
     * @return an instance of the {@link Part} class
     */
    public static PartWithFileContent of(String fileName, String contentType, byte[] content) {
        notNullParam(fileName, "filePath");
        notNullParam(contentType, "contentType");
        notNullParam(content, CONTENT_ARGUMENT);

        return new PartWithFileContent(fileName, content).contentType(contentType);
    }

    /**
     * Adds a header.
     *
     * @param name - the header name
     * @param value - the header value
     * @return an instance of the {@link Part} class
     */
    @SuppressWarnings("unchecked")
    public T header(String name, String value) {
        notNullParam(name, "key");
        notNullParam(value, "value");

        this.headers.put(name.toLowerCase(), value);
        return (T) this;
    }

    /**
     * Adds headers.
     *
     * @param headers - the headers to add
     * @return an instance of the {@link Part} class
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
     * Set the value of the Content-Type header.
     *
     * @param mimeType - the value to be set as the value of the Content-Type header
     * @return an instance of the {@link Part} class
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

