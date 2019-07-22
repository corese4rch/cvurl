package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpMethod;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.multipart.MultipartBody;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.lang.String.format;

/**
 * Builder used to build {@link Request} with body. Used for all methods except GET.
 */
public class RequestWithBodyBuilder extends RequestBuilder<RequestWithBodyBuilder> {

    private static final String MULTIPART_HEADER_TEMPLATE = "multipart/%s;boundary=%s";

    RequestWithBodyBuilder(String uri, HttpMethod method, GenericMapper genericMapper, HttpClient httpClient) {
        super(uri, method, genericMapper, httpClient);
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
        bodyPublisher = HttpRequest.BodyPublishers.ofString(genericMapper.writeValue(body));
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
}
