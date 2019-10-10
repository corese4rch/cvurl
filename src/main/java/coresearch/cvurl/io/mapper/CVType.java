package coresearch.cvurl.io.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class that describes type with generics. Used as parameter to some of {@link coresearch.cvurl.io.request.Request}
 * methods to provide ability to parse response to body to class with generics.
 * Usage example: <br/>
 * {@code cvurl.get(url).asObject(new CVType<List<Users>() {});} <br/>
 * this code snippet would parse response body to {@code List<User>}
 */
public abstract class CVType<T> {
    private Type type;

    protected CVType() {
        Type superclass = this.getClass().getGenericSuperclass();
        if (((ParameterizedType) superclass).getRawType() != CVType.class) {
            throw new IllegalStateException("Type should be direct child type of CVType");
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