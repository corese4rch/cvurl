package coresearch.cvurl.io.utils;

import java.nio.file.Path;

import static java.lang.String.format;

public final class Resources {

    private static final String RESOURCE_PATH = "src/test/resources";

    private Resources() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", Resources.class.getName()));
    }

    public static Path get(String resource) {
        return Path.of(RESOURCE_PATH, resource);
    }
}
