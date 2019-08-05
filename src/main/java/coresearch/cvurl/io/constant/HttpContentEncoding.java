package coresearch.cvurl.io.constant;

import static java.lang.String.format;

public class HttpContentEncoding {
    public static final String GZIP = "gzip";

    private HttpContentEncoding() {
        throw new IllegalStateException(format("Creating of class %s is forbidden", HttpHeader.class.getName()));
    }
}
