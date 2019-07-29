package coresearch.cvurl.io.mapper;

import coresearch.cvurl.io.exception.MappingException;
import coresearch.cvurl.io.exception.ResponseMappingException;
import coresearch.cvurl.io.model.Response;

/**
 * Mapper from/to String to/from some custom type. Used by {@link coresearch.cvurl.io.request.Request}
 * to map body from response to object of some type and by {@link coresearch.cvurl.io.request.RequestBuilder}
 * to convert objects to String request body.
 */
public abstract class GenericMapper {

    public final <T> T readResponseBody(Response<String> response, Class<T> type) {
        try {
            return readValue(response.getBody(), type);
        } catch (MappingException e) {
            throw new ResponseMappingException(e.getMessage(), e, response);
        }
    }

    /**
     * Deserialize String value to object of specified type.
     *
     * @param value     value to be converted.
     * @param valueType type to object of which value should be converted.
     * @param <T>       concrete type
     * @return converted object
     */
    public abstract <T> T readValue(String value, Class<T> valueType);

    /**
     * Serialize object to String.
     *
     * @param value object to be serialized
     * @return resulted String value.
     */
    public abstract String writeValue(Object value);
}
