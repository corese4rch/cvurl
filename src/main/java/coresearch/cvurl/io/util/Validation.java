package coresearch.cvurl.io.util;

import java.util.Objects;

import static java.lang.String.format;

public class Validation {

    private static final String ERROR_MESSAGE = "%s parameter cannot be null";

    public static <T> T notNullParam(T obj) {
        return notNullParam(obj, obj.getClass().getSimpleName());
    }

    public static <T> T notNullParam(T obj, String paramName) {
        return Objects.requireNonNull(obj, format(ERROR_MESSAGE, paramName));
    }
}
