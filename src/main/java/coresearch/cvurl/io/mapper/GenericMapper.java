package coresearch.cvurl.io.mapper;

/**
 * Mapper from/to String to/from some custom type. Used by {@link coresearch.cvurl.io.request.Request}
 * to map body from response to object of some type and by {@link coresearch.cvurl.io.request.RequestBuilder}
 * to convert objects to String request body.
 */
public interface GenericMapper {

    /**
     * Deserialize String value to object of specified type.
     *
     * @param value     value to be converted.
     * @param valueType type to object of which value should be converted.
     * @param <T>       concrete type
     * @return converted object
     */
    <T> T readValue(String value, Class<T> valueType);

    /**
     * Serialize object to String.
     *
     * @param value object to be serialized
     * @return resulted String value.
     */
    String writeValue(Object value);
}
