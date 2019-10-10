package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.CVType;
import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interface that describes protocol for sending HTTP requests.
 */
public interface Request {

    /**
     * Sends current request asynchronously. If response status code
     * matches provided status code then returns {@link CompletableFuture}
     * with object of provided type. Otherwise returns {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return {@link CompletableFuture} with object of provided type or {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}
     */
    <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode);

    /**
     * Sends current request asynchronously. If response status code
     * matches provided status code then returns {@link CompletableFuture}
     * with object of provided type. Otherwise returns {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}.
     * Should be used when converting to class with generics.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return {@link CompletableFuture} with object of provided type or {@link CompletableFuture}
     * that finishes exceptionally with {@link UnexpectedResponseException}
     */
    <T> CompletableFuture<T> asyncAsObject(CVType<T> type, int statusCode);

    /**
     * Sends current request asynchronously.
     *
     * @param type type of object to convert response body.
     * @param <T>  type of object to convert response body
     * @return {@link CompletableFuture} with object of provided type or {@link CompletableFuture}
     * that finishes exceptionally with {@link coresearch.cvurl.io.exception.ResponseMappingException} or
     * {@link RequestExecutionException}
     */
    <T> CompletableFuture<T> asyncAsObject(Class<T> type);

    /**
     * Sends current request asynchronously. Should be used when converting to class with generics.
     *
     * @param type type of object to convert response body.
     * @param <T>  type of object to convert response body
     * @return {@link CompletableFuture} with object of provided type or {@link CompletableFuture}
     * that finishes exceptionally with {@link coresearch.cvurl.io.exception.ResponseMappingException} or
     * {@link RequestExecutionException}
     */
    <T> CompletableFuture<T> asyncAsObject(CVType<T> type);

    /**
     * Sends current request asynchronously.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    CompletableFuture<Response<String>> asyncAsString();

    /**
     * Sends current request asynchronously.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph);

    /**
     * Sends current request asynchronously. Returns response with body as {@link InputStream}
     *
     * @return {@link CompletableFuture} with returned response.
     */
    CompletableFuture<Response<InputStream>> asyncAsStream();

    /**
     * Sends current request asynchronously. Returns response with body as {@link InputStream}
     *
     * @return {@link CompletableFuture} with returned response.
     */
    CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph);

    /**
     * Sends current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler);

    /**
     * Sends current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @return {@link CompletableFuture} with returned response.
     */
    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph);

    /**
     * Sends current request blocking if necessary to get
     * the response. Converts response body to specified type if
     * provided statusCode matches response status code and returns empty optional otherwise.
     * Is some error happens during request sending or response body conversion returns empty optional.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return object of specified type
     */
    <T> Optional<T> asObject(Class<T> type, int statusCode);

    /**
     * Sends current request blocking if necessary to get
     * the response. Converts response body to specified type if
     * provided statusCode matches response status code and returns empty optional otherwise.
     * Is some error happens during request sending or response body conversion returns empty optional.
     * Should be used when converting to class with generics.
     *
     * @param type       type of object to convert response body.
     * @param statusCode status code on which converting should be done
     * @param <T>        type of object to convert response body
     * @return object of specified type
     */
    <T> Optional<T> asObject(CVType<T> type, int statusCode);

    /**
     * Sends current request blocking if necessary to get
     * the response. Converts response body to specified type, if error happens during conversion
     * throws {@link coresearch.cvurl.io.exception.ResponseMappingException}.
     *
     * @param type type of object to convert response body.
     * @param <T>  type of object to convert response body
     * @return object of specified type
     */
    <T> T asObject(Class<T> type);

    /**
     * Sends current request blocking if necessary to get
     * the response. Converts response body to specified type, if error happens during conversion
     * throws {@link coresearch.cvurl.io.exception.ResponseMappingException}.
     * Should be used when converting to class with generics.
     *
     * @param type type of object to convert response body.
     * @param <T>  type of object to convert response body
     * @return object of specified type
     */
    <T> T asObject(CVType<T> type);

    /**
     * Sends current request blocking if necessary to get
     * the response.
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    Optional<Response<String>> asString();

    /**
     * Sends current request blocking if necessary to get
     * the response as {@link InputStream}
     *
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    Optional<Response<InputStream>> asStream();

    /**
     * Sends current request blocking if necessary to get
     * the response with body parsed by provided bodyHandler.
     *
     * @param bodyHandler used to parse response body
     * @return {@link Optional} with response if request no error happened during
     * request sending or empty {@link Optional} otherwise.
     */
    <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler);
}
