package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MultipartType;
import coresearch.cvurl.io.exception.BadFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static coresearch.cvurl.io.util.Validation.notNullParam;
import static java.nio.charset.StandardCharsets.UTF_8;

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

    public static MultipartBody create() {
        return create(UUID.randomUUID().toString());
    }

    public static MultipartBody create(String boundary) {
        notNullParam(boundary, "boundary");

        return new MultipartBody(boundary, MultipartType.MIXED, new ArrayList<>());
    }

    public List<byte[]> asByteArrays() {
        var result = parts.stream()
                .flatMap(part -> (Stream<byte[]>) part.asByteArrays(boundary).stream())
                .collect(Collectors.toList());

        result.add((BOUNDARY_DELIMITER + boundary + BOUNDARY_DELIMITER).getBytes(UTF_8));

        return result;
    }

    public String getMultipartType() {
        return multipartType;
    }

    public String getBoundary() {
        return boundary;
    }

    public MultipartBody type(String multipartType) {
        notNullParam(multipartType, "multipartType");

        this.multipartType = multipartType;
        return this;
    }

    public MultipartBody part(Part part) {
        notNullParam(part, "part");

        this.parts.add(part);
        return this;
    }

    public MultipartBody formPart(String name, Part part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        this.parts.add(part.header(HttpHeader.CONTENT_DISPOSITION, getContentDispositionHeader(name)));
        return this;
    }

    public MultipartBody formPart(String name, PartWithFileContent part) {
        notNullParam(name, "name");
        notNullParam(part, "part");

        return formPart(name, part.getFilePath().getFileName().toString(), part);
    }

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
                throw new BadFileException(e.getMessage(), e.getCause());
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

