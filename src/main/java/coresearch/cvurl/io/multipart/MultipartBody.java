package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MultipartType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The class for building a multipart request body.
 *
 * @since 1.0
 */
public class MultipartBody {

    private static final String CONTENT_DISPOSITION_TEMPLATE = "form-data; name=\"%s\"";
    private static final String CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE = CONTENT_DISPOSITION_TEMPLATE + "; filename=\"%s\"";
    private static final String BOUNDARY_DELIMITER = "--";

    private final String boundary;
    private String multipartType;
    private final List<Part> parts;

    private MultipartBody(String boundary, String multipartType, List<Part> parts) {
        this.boundary = boundary;
        this.multipartType = multipartType;
        this.parts = parts;
    }

    /**
     * Creates a new instance of the {@link MultipartBody} class with a randomly generated boundary.
     *
     * @return an instance of the {@link MultipartBody} class
     */
    public static MultipartBody create() {
        return create(UUID.randomUUID().toString());
    }

    /**
     * Creates a new instance of the {@link MultipartBody} class with the provided boundary.
     *
     * @param boundary - the value used as the border
     * @return an instance of the {@link MultipartBody} class
     */
    public static MultipartBody create(String boundary) {
        notNullParam(boundary, "boundary");

        return new MultipartBody(boundary, MultipartType.MIXED, new ArrayList<>());
    }

    /**
     * Generates a multipart body as a list of byte arrays.
     *
     * @return the list of byte arrays
     */
    public List<byte[]> asByteArrays() {
        var result = parts.stream()
                .flatMap(part -> (Stream<byte[]>) part.asByteArrays(boundary).stream())
                .collect(Collectors.toList());

        result.add((BOUNDARY_DELIMITER + boundary + BOUNDARY_DELIMITER).getBytes(UTF_8));

        return result;
    }

    /**
     * Returns the {@code multipartType} value.
     */
    public String getMultipartType() {
        return multipartType;
    }

    /**
     * Returns the {@code boundary} value.
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Sets the multipart type.
     *
     * @param multipartType - the type to be set.
     * @return the same instance of the {@link MultipartBody} class
     */
    public MultipartBody type(String multipartType) {
        notNullParam(multipartType, "multipartType");

        this.multipartType = multipartType;
        return this;
    }

    /**
     * Adds an instance of the {@link Part} class to the body.
     *
     * @param part - the instance of the {@link Part} class
     * @return the same instance of the {@link MultipartBody} class
     */
    public MultipartBody part(Part part) {
        notNullParam(part, "part");

        this.parts.add(part);
        return this;
    }

    /**
     * Adds an instance of the {@link Part} class to the body with the provided name.
     *
     * @param name - the name of the part
     * @param part - the instance of the {@link Part} class
     * @return the same instance of the {@link MultipartBody} class
     */
    public MultipartBody formPart(String name, Part part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        this.parts.add(part.header(HttpHeader.CONTENT_DISPOSITION, getContentDispositionHeader(name)));
        return this;
    }

    /**
     * Adds an instance of the {@link PartWithFileContent} class to the body with the provided name.
     *
     * @param name - the name of the part
     * @param part - the instance of the {@link PartWithFileContent} class
     * @return the same instance of the {@link MultipartBody} class
     */
    public MultipartBody formPart(String name, PartWithFileContent part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        part.header(HttpHeader.CONTENT_DISPOSITION, getContentDispositionHeader(name, part.getFileName()));
        this.parts.add(part);

        return this;
    }

    private String getContentDispositionHeader(String name) {
        return String.format(CONTENT_DISPOSITION_TEMPLATE, name);
    }

    private String getContentDispositionHeader(String name, String filename) {
        return String.format(CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE, name, filename);
    }
}