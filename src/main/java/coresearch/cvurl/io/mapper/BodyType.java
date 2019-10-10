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
    private Type type;

    protected BodyType() {
        Type superclass = this.getClass().getGenericSuperclass();
        if (((ParameterizedType) superclass).getRawType() != BodyType.class) {
            throw new IllegalStateException("Type should be direct child type of BodyType");
        }

        ParameterizedType type = ((ParameterizedType) superclass);
        this.type = type.getActualTypeArguments()[0];
    }

    /**
     * @return actual type
     */
    public Type getType() {
        return type;
    }
}