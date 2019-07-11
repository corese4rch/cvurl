package coresearch.cvurl.io.util;

import coresearch.cvurl.io.exception.BadUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import static coresearch.cvurl.io.util.Validation.notNullParam;

/**
 * Class for building urls. Its only purpose is to build url endpoint and it doesn't handle query params.
 * If you want to add query params to your request use
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParam(String, String)} or
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParams(Map)} method.
 */
public class Url {

    private static final Pattern DOUBLE_SLASHES_PATTERN = Pattern.compile("(?<!(http:|https:))/{2,}");
    private static final Pattern WHITESPACES_PATTERN = Pattern.compile("\\s+");

    private final String url;

    private Url(String url) {
        this.url = url;
    }

    /**
     * Creates new Url object from provided url.
     *
     * @param url provided endpoint url
     * @return new Url object
     */
    public static Url of(String url) {
        notNullParam(url, "url");
        return new Url(url);
    }

    /**
     * Creates new Url object with given endpoint schema and host.
     *
     * @param schema provided endpoint schema
     * @param host provided endpoint host
     * @return new Url object
     */
    public static Url of(String schema, String host) {
        notNullParam(schema, "schema");
        notNullParam(host, "host");
        return new Url(schema.strip() + "://" + host);
    }

    /**
     * Adds path to url and returns new Url object.
     *
     * @param path provided path
     * @return new Url object build out of current url + "/" + path
     */
    public Url path(String path) {
        notNullParam(path, "path");
        return new Url(this.url + "/" + path);
    }

    /**
     * Returns url as {@link URL}. Removes from url redundant whitespaces and slashes.
     * In case of malformed url throws {@link BadUrlException}.
     *
     * @return url as {@link URL}
     */
    public URL create() {
        try {
            return new URL(removeRedundantSlashesAndWhitespaces(url));
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

