package coresearch.cvurl.io.request;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.util.HttpHeader;
import coresearch.cvurl.io.util.HttpMethod;
import coresearch.cvurl.io.util.MIMEType;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Collection;

/**
 * Builder used to build {@link Request} with body. Used for all methods except GET.
 */
public class RequestWithBodyBuilder extends RequestBuilder<RequestWithBodyBuilder> {

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
     * Sets request body.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(Collection<Object> body) {
        bodyPublisher = HttpRequest.BodyPublishers.ofString(genericMapper.writeValue(body));
        header(HttpHeader.CONTENT_TYPE, MIMEType.APPLICATION_JSON);
        return this;
    }
}
