package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MultipartType;
import coresearch.cvurl.io.exception.MultipartFileFormException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Class for building multipart request body.
 */
public class MultipartBody {

    private static final String CONTENT_DISPOSITION_TEMPLATE = "form-data; name=\"%s\"";
    private static final String CONTENT_DISPOSITION_WITH_FILENAME_TEMPLATE = CONTENT_DISPOSITION_TEMPLATE + "; filename=\"%s\"";
    private static final String BOUNDARY_DELIMITER = "--";

    private String boundary;
    private String multipartType;
    private List<Part> parts;

    private MultipartBody(String boundary, String multipartType, List<Part> parts) {
        this.boundary = boundary;
        this.multipartType = multipartType;
        this.parts = parts;
    }

    /**
     * Creates new instance of {@link MultipartBody} with randomly generated boundary.
     *
     * @return new instance of {@link MultipartBody}
     */
    public static MultipartBody create() {
        return create(UUID.randomUUID().toString());
    }

    /**
     * Creates new instance of {@link MultipartBody} with provided boundary.
     *
     * @return new instance of {@link MultipartBody}
     */
    public static MultipartBody create(String boundary) {
        notNullParam(boundary, "boundary");

        return new MultipartBody(boundary, MultipartType.MIXED, new ArrayList<>());
    }

    /**
     * Generate multipart body as byte array later to be used by {@link coresearch.cvurl.io.request.RequestWithBodyBuilder}
     *
     * @return list of byte arrays
     */
    public List<byte[]> asByteArrays() {
        var result = parts.stream()
                .flatMap(part -> (Stream<byte[]>) part.asByteArrays(boundary).stream())
                .collect(Collectors.toList());
        result.add((BOUNDARY_DELIMITER + boundary + BOUNDARY_DELIMITER).getBytes(UTF_8));
        return result;
    }

    /**
     * Returns multipart type of {@link MultipartBody}
     *
     * @return multipart type
     */
    public String getMultipartType() {
        return multipartType;
    }

    /**
     * Returns boundary of {@link MultipartBody}
     *
     * @return boundary
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Sets multipart type of {@link MultipartBody}
     *
     * @param multipartType multipart type to be set.
     * @return this {@link MultipartBody}
     */
    public MultipartBody type(String multipartType) {
        notNullParam(multipartType, "multipartType");

        this.multipartType = multipartType;
        return this;
    }

    /**
     * Add a part to the body.
     *
     * @param part part to add
     * @return this {@link MultipartBody}
     */
    public MultipartBody part(Part part) {
        notNullParam(part, "part");

        this.parts.add(part);
        return this;
    }

    /**
     * Add a form data part to the body with provided part name.
     *
     * @param name part name
     * @param part part to add
     * @return this {@link MultipartBody}
     */
    public MultipartBody formPart(String name, Part part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        this.parts.add(part.header(HttpHeader.CONTENT_DISPOSITION, getContentDispositionHeader(name)));
        return this;
    }

    /**
     * Add a file form data part to the body with provided part name.Use name of provided file as value for filename field
     * If Content-type is not previously set detect content-type from file.
     *
     * @param name part name
     * @param part part to add
     * @return this {@link MultipartBody}
     */
    public MultipartBody formPart(String name, PartWithFileContent part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        return formPart(name, part.getFilePath().getFileName().toString(), part);
    }

    /**
     * Add a file form data part to the body with provided part name.Use provided filename as value for filename field
     * If Content-type is not previously set detect content-type from file.
     *
     * @param name part name
     * @param filename value of filename field
     * @param part part to add
     * @return this {@link MultipartBody}
     */
    public MultipartBody formPart(String name, String filename, PartWithFileContent part) {
        notNullParam(name, "name");
        notNullParam(filename, "filename");
        notNullParam(part, "part");

        var path = part.getFilePath();
        part.header(HttpHeader.CONTENT_DISPOSITION, getContentDispositionHeader(name, filename));

        if (!part.isContentTypeSet()) {
            try {
                part.contentType(Files.probeContentType(path));
            } catch (IOException e) {
                throw new MultipartFileFormException(e.getMessage(), e.getCause());
            }
        }

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

