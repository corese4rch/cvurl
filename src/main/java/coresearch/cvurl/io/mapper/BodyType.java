package coresearch.cvurl.io.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class that describes type with generics. Used as parameter to some of {@link coresearch.cvurl.io.request.Request}
 * methods to provide ability to parse response to body to class with generics.
 * Usage example: <br/>
 * {@code cvurl.get(url).asObject(new BodyType<List<Users>() {});} <br/>
 * this code snippet would parse response body to {@code List<User>}
 */
public abstract class BodyType<T> {

    private static final String ERROR_MESSAGE = "Type should be direct child type of BodyType";

    private Type type;

    protected BodyType() {
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        if (superclass.getRawType() != BodyType.class) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }
        this.type = superclass.getActualTypeArguments()[0];
    }

    /**
     * @return actual type
     */
    public Type getType() {
        return type;
    }
}