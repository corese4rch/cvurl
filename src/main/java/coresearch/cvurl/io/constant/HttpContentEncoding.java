package coresearch.cvurl.io.constant;

import static java.lang.String.format;

/**
 * Contains constant definitions for the HTTP content encodings.
 *
 * @since 0.9
 */
public final class HttpContentEncoding {

    /** The HTTP {@code gzip} content encoding. */
    public static final String GZIP = "gzip";

    private HttpContentEncoding() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", HttpHeader.class.getName()));
    }
}
