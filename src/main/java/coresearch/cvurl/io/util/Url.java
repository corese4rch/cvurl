package coresearch.cvurl.io.util;

import coresearch.cvurl.io.exception.BadUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import static coresearch.cvurl.io.internal.util.Validation.notNullParam;

/**
 * Class for building URLs. Its sole purpose is to create a URL object. It does not process query parameters.
 * If you want to add query params to your request, use the
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParam(String, String)} or
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParams(Map)} methods.
 *
 * @since 0.9
 */
public class Url {

    private static final Pattern DOUBLE_SLASHES_PATTERN = Pattern.compile("(?<!(http:|https:))/{2,}");
    private static final Pattern WHITESPACES_PATTERN = Pattern.compile("\\s+");

    private final String baseUrl;

    private Url(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Creates a new {@link Url} object based on the provided URL.
     *
     * @param url - the provided URL
     * @return the {@link Url} object
     */
    public static Url of(String url) {
        notNullParam(url, "url");
        return new Url(url);
    }

    /**
     * Creates a new {@link Url} object based on the provided schema and host.
     *
     * @param schema - the provided schema
     * @param host - the provided host
     * @return the {@link Url} object
     */
    public static Url of(String schema, String host) {
        notNullParam(schema, "schema");
        notNullParam(host, "host");
        return new Url(schema.strip() + "://" + host);
    }

    /**
     * Adds a URL path and returns a new {@link Url} object.
     *
     * @param path - the provided path
     * @return the {@link Url} object created based on current URL + "/" + path
     */
    public Url path(String path) {
        notNullParam(path, "path");
        return new Url(this.baseUrl + "/" + path);
    }

    /**
     * Returns the {@link Url} as a {@link URL} object. Removes extra spaces and slashes from the URL.
     *
     * @return the {@link Url} as a {@link URL} object
     * @throws BadUrlException in case of invalid URL
     */
    public URL create() {
        try {
            return new URL(removeRedundantSlashesAndWhitespaces(baseUrl));
        } catch (MalformedURLException e) {
            throw new BadUrlException(e.getMessage(), e);
        }
    }

    private static String removeRedundantSlashesAndWhitespaces(String str) {
        return DOUBLE_SLASHES_PATTERN
                .matcher(WHITESPACES_PATTERN.matcher(str).replaceAll(""))
                .replaceAll("/");
    }
}

