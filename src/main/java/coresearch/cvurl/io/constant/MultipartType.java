package coresearch.cvurl.io.constant;

import static java.lang.String.format;

/**
 * Contains constant definitions for the HTTP multipart types.
 *
 * @since 1.0
 */
public final class MultipartType {

    /** The HTTP {@code mixed} multipart type. */
    public static final String MIXED = "mixed";

    /** The HTTP {@code form-data} multipart type. */
    public static final String FORM = "form-data";

    /** The HTTP {@code alternative} multipart type. */
    public static final String ALTERNATIVE = "alternative";

    /** The HTTP {@code digest} multipart type. */
    public static final String DIGEST = "digest";

    /** The HTTP {@code parallel} multipart type. */
    public static final String PARALLEL = "parallel";

    private MultipartType() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", MultipartType.class.getName()));
    }
}
