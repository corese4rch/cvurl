package coresearch.cvurl.io.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    public Type getType() {
        return type;
    }
}