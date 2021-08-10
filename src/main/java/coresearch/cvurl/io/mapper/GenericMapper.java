package coresearch.cvurl.io.mapper;

import coresearch.cvurl.io.exception.MappingException;
import coresearch.cvurl.io.exception.ResponseMappingException;
import coresearch.cvurl.io.model.Response;

/**
 * Provides functionality for reading and writing JSON, either to and from basic POJOs (Plain Old Java Objects).
 * Used by the {@link coresearch.cvurl.io.request.Request} and {@link coresearch.cvurl.io.request.RequestBuilder} classes.
 *
 * @since 0.9
 */
public abstract class GenericMapper {

    /**
     * Deserializes a response body to an object of specified type.
     *
     * @param response - the response whose body needs to be converted.
     * @param type - the type to convert the value to.
     * @param <T> - the concrete type
     * @return a value converted to the specified type
     * @throws ResponseMappingException in case of issues with mapping the response body to a specific type
     */
    public final <T> T readResponseBody(Response<String> response, Class<T> type) {
        try {
            return readValue(response.getBody(), type);
        } catch (MappingException e) {
            throw new ResponseMappingException(e.getMessage(), e, response);
        }
    }

    /**
     * Deserializes a response body to an object of specified type.
     *
     * @param response - the response whose body needs to be converted.
     * @param type - the type to convert the value to.
     * @param <T> - the concrete type
     * @return a value converted to the specified type
     * @throws ResponseMappingException in case of issues with mapping the response body to a specific type
     */
    public final <T> T readResponseBody(Response<String> response, BodyType<T> type) {
        try {
            return readValue(response.getBody(), type);
        } catch (MappingException e) {
            throw new ResponseMappingException(e.getMessage(), e, response);
        }
    }

    /**
     * Deserializes a String value to an object of specified type.
     *
     * @param value - the value to be converted.
     * @param valueType - the type to convert the value to.
     * @param <T> - the concrete type
     * @return a value converted to the specified type
     */
    public abstract <T> T readValue(String value, Class<T> valueType);

    /**
     * Deserializes a String value to an object of the specified type. It should be used when you need to deserialize a generic type.
     *
     * @param value - the value to be converted.
     * @param valueType - the type to convert the value to.
     * @param <T> - the concrete type
     * @return a value converted to the specified type
     */
    public abstract <T> T readValue(String value, BodyType<T> valueType);

    /**
     * Serializes an object to String.
     *
     * @param value - the object to be serialized
     * @return the resulted String value.
     */
    public abstract String writeValue(Object value);
}
