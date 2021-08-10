package coresearch.cvurl.io.constant;

/**
 * Enumeration of HTTP request methods. Intended for use
 * with {@link coresearch.cvurl.io.request.RequestBuilder}
 *
 * @since 0.9
 */
public enum HttpMethod {

    /**
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
     */
    GET,

    /**
     * The PUT method replaces all current representations of the target resource with the request payload.
     */
    PUT,

    /**
     * The POST method is used to submit an entity to the specified resource,
     * often causing a change in state or side effects on the server.
     */
    POST,

    /**
     * The DELETE method deletes the specified resource.
     */
    DELETE,

    /**
     * The PATCH method is used to apply partial modifications to a resource.
     */
    PATCH,

    /**
     * The OPTIONS method is used to describe the communication options for the target resource.
     */
    OPTIONS,

    /**
     * The HEAD method asks for a response identical to that of a GET request, but without the response body.
     */
    HEAD
}
