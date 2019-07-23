package coresearch.cvurl.io.utils;

import java.nio.file.Path;

public class Resources {

    private static final String RESOURCE_PATH = "src/test/resources";

    public static Path get(String resource) {
        return Path.of(RESOURCE_PATH, resource);
    }
}
