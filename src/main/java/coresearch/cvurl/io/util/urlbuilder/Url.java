package coresearch.cvurl.io.util.urlbuilder;

import coresearch.cvurl.io.exception.BadUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Class for building urls. Its only purpose is to build url endpoint and it doesn't handle query params.
 * If you want to add query params to your request use
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParam(String, String)} or
 * {@link coresearch.cvurl.io.request.RequestBuilder#queryParams(Map)} method.
 */
public class Url {

    private final String url;

    private Url(String url) {
        this.url = url;
    }

    /**
     * Creates new Url object with given endpoint url after removing trailing and leading whitespaces and slashes from it.
     *
     * @param url provided endpoint url
     * @return new Url object
     */
    public static Url of(String url) {
        return new Url(stripSlashesAndWhiteSpaces(url));
    }

    /**
     * Creates new Url object with given endpoint schema and host. Parse schema and host into proper form.
     *
     * @param schema provided endpoint schema
     * @param host provided endpoint host
     * @return new Url object
     */
    public static Url of(String schema, String host) {
        return new Url(
                schema.strip().replaceFirst("[/:]+$", "") + "://" + stripSlashesAndWhiteSpaces(host));
    }

    /**
     * Adds path to url and returns new Url object. Removes trailing and leading whitespaces and slashes from path.
     *
     * @param path provided path
     * @return new Url object build out of current url + "/" + path
     */
    public Url path(String path) {
        String newUrl = this.url + "/" + stripSlashesAndWhiteSpaces(path);

        return new Url(newUrl);
    }

    /**
     * Returns url as {@link URL}. In case of malformed url throws {@link BadUrlException}.
     *
     * @return url as {@link URL}
     */
    public URL create() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadUrlException(e.getMessage(), e);
        }
    }

    private static String stripSlashesAndWhiteSpaces(String str) {
        return str.strip()
                .replaceFirst("^/+", "")
                .replaceFirst("/+$", "");
    }
}

