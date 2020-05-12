package coresearch.cvurl.io.request;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.model.CVurlConfig;
import coresearch.cvurl.io.multipart.MultipartBody;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

/**
 * Builder used to build {@link Request} with body. Used for all methods except GET.
 */
public class RequestWithBodyBuilder extends RequestBuilder<RequestWithBodyBuilder> {

    private static final String MULTIPART_HEADER_TEMPLATE = "multipart/%s;boundary=%s";

    RequestWithBodyBuilder(String uri, HttpMethod method, CVurlConfig cvurlConfig) {
        super(uri, method, cvurlConfig);
    }

    /**
     * Sets request body.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(String body) {
        bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        return this;
    }

    /**
     * Sets request body.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(byte[] body) {
        bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(body);
        return this;
    }

    /**
     * Sets request body.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(Object body) {
        bodyPublisher = HttpRequest.BodyPublishers.ofString(cvurlConfig.getGenericMapper().writeValue(body));
        header(HttpHeader.CONTENT_TYPE, MIMEType.APPLICATION_JSON);
        return this;
    }

    /**
     * Sets request body as multipart data. Sets content-type header as multipart/{multipartType}
     *
     * @param multipartBody request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(MultipartBody multipartBody) {
        bodyPublisher = HttpRequest.BodyPublishers.ofByteArrays(multipartBody.asByteArrays());
        header(HttpHeader.CONTENT_TYPE,
                format(MULTIPART_HEADER_TEMPLATE, multipartBody.getMultipartType(), multipartBody.getBoundary()));
        return this;
    }

    /**
     * Sets request body as application/x-www-form-urlencoded. Sets Content-type header
     * to application/x-www-form-urlencoded.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder formData(Map<?, ?> body) {
        if (body.isEmpty()) {
            throw new IllegalStateException("Form data map shouldn't be empty");
        }

        var formData = body.entrySet().stream()
                .map(entry -> encodeToUTF8(entry.getKey()) + "=" + encodeToUTF8(entry.getValue()))
                .collect(joining("&"));

        bodyPublisher = HttpRequest.BodyPublishers.ofString(formData);
        header(HttpHeader.CONTENT_TYPE, MIMEType.APPLICATION_FORM);
        return this;
    }

    private String encodeToUTF8(Object obj) {
        return URLEncoder.encode(obj.toString(), UTF_8);
    }
}
