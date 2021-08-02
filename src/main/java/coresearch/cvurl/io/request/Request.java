package coresearch.cvurl.io.request;

import coresearch.cvurl.io.exception.ResponseMappingException;
import coresearch.cvurl.io.exception.UnexpectedResponseException;
import coresearch.cvurl.io.mapper.BodyType;
import coresearch.cvurl.io.model.Response;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The interface describes a protocol for sending HTTP requests.
 *
 * @since 0.9
 */
public interface Request {

    /**
     * Sends the current request asynchronously. If the response status code matches the provided code,
     * it returns the instance of the {@link CompletableFuture} class with the object of provided type.
     * Otherwise, it returns the instance of the {@link CompletableFuture} class that
     * terminates exceptionally with {@link UnexpectedResponseException}.
     *
     * @param type - the type of object to which the response body should be converted
     * @param statusCode - the status code which is considered successful
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link CompletableFuture} class with the object of provided type
     * or an instance of the {@link CompletableFuture} class that
     * terminates exceptionally with {@link UnexpectedResponseException}
     */
    <T> CompletableFuture<T> asyncAsObject(Class<T> type, int statusCode);

    /**
     * Sends the current request asynchronously. If the response status code matches the provided code,
     * it returns the instance of the {@link CompletableFuture} class with the object of provided type.
     * Otherwise, it returns the instance of the {@link CompletableFuture} class that
     * terminates exceptionally with {@link UnexpectedResponseException}.
     *
     * @param type - the type of object to which the response body should be converted
     * @param statusCode - the status code which is considered successful
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link CompletableFuture} class with the object of provided type
     * or an instance of the {@link CompletableFuture} class
     * that terminates exceptionally with {@link UnexpectedResponseException}
     */
    <T> CompletableFuture<T> asyncAsObject(BodyType<T> type, int statusCode);

    /**
     * Sends the current request asynchronously.
     *
     * @param type - the type of object to which the response body should be converted
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link CompletableFuture} class with the object of provided type
     * or an instance of the {@link CompletableFuture} class that terminates exceptionally
     * with {@link UnexpectedResponseException} or {@link coresearch.cvurl.io.exception.ResponseMappingException}
     */
    <T> CompletableFuture<T> asyncAsObject(Class<T> type);

    /**
     * Sends the current request asynchronously.
     *
     * @param type - the type of object to which the response body should be converted
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link CompletableFuture} class with the object of provided type
     * or an instance of the {@link CompletableFuture} class that terminates exceptionally
     * with {@link UnexpectedResponseException} or {@link coresearch.cvurl.io.exception.ResponseMappingException}
     */
    <T> CompletableFuture<T> asyncAsObject(BodyType<T> type);

    /**
     * Sends the current request asynchronously.
     *
     * @return an instance of the {@link CompletableFuture} class with a response that contains body as a string.
     */
    CompletableFuture<Response<String>> asyncAsString();

    /**
     * Sends the current request asynchronously.
     *
     * @param pph - the push promise handler
     * @return an instance of the {@link CompletableFuture} class with a response that contains body as a string
     */
    CompletableFuture<Response<String>> asyncAsString(HttpResponse.PushPromiseHandler<String> pph);

    /**
     * Sends the current request asynchronously.
     *
     * @return an instance of the {@link CompletableFuture} class with a response that contains body as {@link InputStream}
     */
    CompletableFuture<Response<InputStream>> asyncAsStream();

    /**
     * Sends the current request asynchronously.
     *
     * @param pph - the push promise handler
     * @return an instance of the {@link CompletableFuture} class with a response that contains body as {@link InputStream}
     */
    CompletableFuture<Response<InputStream>> asyncAsStream(HttpResponse.PushPromiseHandler<InputStream> pph);

    /**
     * Sends the current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @param bodyHandler - the body handler that applies to the response body
     * @return an instance of the {@link CompletableFuture} class with a response that contains body
     * converted by the provided body handler
     */
    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler);

    /**
     * Sends the current request asynchronously. Applies provided bodyHandler to the response body.
     *
     * @param bodyHandler - the body handler that applies to the response body
     * @param pph - the push promise handler
     * @return an instance of the {@link CompletableFuture} class with a response that contains body
     * converted by the provided body handler
     */
    <T> CompletableFuture<Response<T>> asyncAs(HttpResponse.BodyHandler<T> bodyHandler, HttpResponse.PushPromiseHandler<T> pph);

    /**
     * Sends the current request synchronously. If the response status code matches the provided code,
     * it returns the response body converted to the specified type. Otherwise, an empty optional parameter is returned.
     *
     * @param type - the type of object to which the response body should be converted
     * @param statusCode - the status code which is considered successful
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link Optional} class
     * that contains response body converted to the provided type or empty {@link Optional} on error
     */
    <T> Optional<T> asObject(Class<T> type, int statusCode);

    /**
     * Sends the current request synchronously. If the response status code matches the provided code,
     * it returns the response body converted to the specified type. Otherwise, an empty optional parameter is returned.
     *
     * @param type - the type of object to which the response body should be converted
     * @param statusCode - the status code which is considered successful
     * @param <T> - the type of object to which the response body should be converted
     * @return an instance of the {@link Optional} class
     * that contains response body converted to the provided type or empty {@link Optional} on error
     */
    <T> Optional<T> asObject(BodyType<T> type, int statusCode);

    /**
     * Sends the current request synchronously.
     *
     * @param type - the type of object to which the response body should be converted
     * @param <T> - the type of object to which the response body should be converted
     * @return a response body converted to the specified type
     * @throws ResponseMappingException in case of any issues during the conversion process
     */
    <T> T asObject(Class<T> type);

    /**
     * Sends the current request synchronously.
     *
     * @param type - the type of object to which the response body should be converted
     * @param <T> - the type of object to which the response body should be converted
     * @return a response body converted to the specified type
     * @throws ResponseMappingException in case of any issues during the conversion process
     */
    <T> T asObject(BodyType<T> type);

    /**
     * Sends the current request synchronously.
     *
     * @return an instance of the {@link Optional} class
     *         that contains response body converted to a string or empty {@link Optional} on error
     */
    Optional<Response<String>> asString();

    /**
     * Sends the current request synchronously.
     *
     * @return an instance of the {@link Optional} class
     *         that contains response body converted to an {@link InputStream} instance or empty {@link Optional} on error
     */
    Optional<Response<InputStream>> asStream();

    /**
     * Sends the current request synchronously.
     * @param bodyHandler - the body handler that applies to the response body
     * @return an instance of the {@link Optional} class with a response that contains body
     *         converted by the provided body handler or empty {@link Optional} on error
     */
    <T> Optional<Response<T>> as(HttpResponse.BodyHandler<T> bodyHandler);
}
