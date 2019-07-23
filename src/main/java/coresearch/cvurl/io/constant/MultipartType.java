package coresearch.cvurl.io.constant;

import static java.lang.String.format;

public class MultipartType {
    public static final String MIXED = "mixed";
    public static final String FORM = "form-data";
    public static final String ALTERNATIVE = "alternative";
    public static final String DIGEST = "digest";
    public static final String PARALLEL = "parallel";

    private MultipartType() {
        throw new IllegalStateException(format("Creating of class %s is forbidden", MultipartType.class.getName()));
    }
}
