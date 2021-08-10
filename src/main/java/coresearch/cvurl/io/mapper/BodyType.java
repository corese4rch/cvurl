package coresearch.cvurl.io.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The class helps to describe a generic type. Used as a parameter in some
 * {@link coresearch.cvurl.io.request.Request} methods to provide the ability to parse the response body into a specific type.
 * Usage example: <br/>
 * {@code cvurl.get(url).asObject(new BodyType<List<Users>() {});} <br/>
 * this code snippet could be used to parse a response body to {@code List<User>}
 *
 * @since 1.2
 * @param <T> the data type in response body
 */
public abstract class BodyType<T> {

    private static final String ERROR_MESSAGE = "The type must be a direct child of the BodyType class.";

    private final Type type;

    protected BodyType() {
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();

        if (superclass.getRawType() != BodyType.class) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        this.type = superclass.getActualTypeArguments()[0];
    }

    /**
     * @return the actual type
     */
    public Type getType() {
        return type;
    }
}