package coresearch.cvurl.io.internal.util;

import java.util.Objects;

import static java.lang.String.format;

/**
 * This class consists of {@code static} utility methods to test for null conditions before operation.
 *
 * @since 0.9
 */
public final class Validation {

    private static final String ERROR_MESSAGE = "%s parameter cannot be null";

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * @param objs - the object references to check for nullity
     * @param <T> - the type of the references
     * @throws NullPointerException if any of the {@code objs} is {@code null}
     */
    @SafeVarargs
    public static <T> void notNullParams(T... objs) {
        for (T obj : objs) {
            notNullParam(obj, obj.getClass().getSimpleName());
        }
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * @param obj - the object reference to check for nullity
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T notNullParam(T obj) {
        return notNullParam(obj, obj.getClass().getSimpleName());
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * @param obj - the object reference to check for nullity
     * @param paramName - the parameter name of the checked value
     * @param <T> - the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T notNullParam(T obj, String paramName) {
        return Objects.requireNonNull(obj, format(ERROR_MESSAGE, paramName));
    }

    private Validation() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", Validation.class.getName()));
    }
}
