package coreserech.cvurl.io.request;

import coreserech.cvurl.io.mapper.GenericMapper;
import coreserech.cvurl.io.util.HttpHeader;
import coreserech.cvurl.io.util.HttpMethod;
import coreserech.cvurl.io.util.MIMEType;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Collection;

/**
 * Builder used to build {@link Request} with body. Used for all methods except GET.
 */
public class RequestWithBodyBuilder extends RequestBuilder<RequestWithBodyBuilder> {

    private HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.noBody();

    public RequestWithBodyBuilder(String uri, HttpMethod method, GenericMapper genericMapper, HttpClient httpClient) {
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

    /**
     * Sets request body.
     *
     * @param body request body
     * @return this builder
     */
    public RequestWithBodyBuilder body(JSONObject body) {
        bodyPublisher = HttpRequest.BodyPublishers.ofString(body.toString());
        header(HttpHeader.CONTENT_TYPE, MIMEType.APPLICATION_JSON);
        return this;
    }

    @Override
    protected HttpRequest.Builder setUpHttpRequestBuilder() {
        return super.setUpHttpRequestBuilder().method(method.name(), bodyPublisher);
    }


}
