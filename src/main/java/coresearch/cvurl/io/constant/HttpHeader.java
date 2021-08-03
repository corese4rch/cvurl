package coresearch.cvurl.io.constant;

import static java.lang.String.format;

/**
 * Contains constant definitions for the HTTP header names.
 *
 * @since 0.9
 */
public final class HttpHeader {

    /** The HTTP {@code Accept} header name. */
    public static final String ACCEPT = "Accept";

    /** The HTTP {@code Accept-Charset} header name. */
    public static final String ACCEPT_CHARSET = "Accept-Charset";

    /** The HTTP {@code Accept-Encoding} header name. */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    /** The HTTP {@code Accept-Language} header name. */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    /** The HTTP {@code Accept-Ranges} header name. */
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    /** The HTTP {@code Age} header name. */
    public static final String AGE = "Age";

    /** The HTTP {@code Allow} header name. */
    public static final String ALLOW = "Allow";

    /** The HTTP {@code Authorization} header name. */
    public static final String AUTHORIZATION = "Authorization";

    /** The HTTP {@code Cache-Control} header name. */
    public static final String CACHE_CONTROL = "Cache-Control";

    /** The HTTP {@code Connection} header name. */
    public static final String CONNECTION = "Connection";

    /** The HTTP {@code Content-Encoding} header name. */
    public static final String CONTENT_ENCODING = "Content-Encoding";

    /** The HTTP {@code Content-Language} header name. */
    public static final String CONTENT_LANGUAGE = "Content-Language";

    /** The HTTP {@code Content-Length} header name. */
    public static final String CONTENT_LENGTH = "Content-Length";

    /** The HTTP {@code Content-Location} header name. */
    public static final String CONTENT_LOCATION = "Content-Location";

    /** The HTTP {@code Content-MD5} header name. */
    public static final String CONTENT_MD5 = "Content-MD5";

    /** The HTTP {@code Content-Range} header name. */
    public static final String CONTENT_RANGE = "Content-Range";

    /** The HTTP {@code Content-Type} header name. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** The HTTP {@code Content-Disposition} header name. */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /** The HTTP {@code Date} header name. */
    public static final String DATE = "Date";

    /** The HTTP {@code Dav} header name. */
    public static final String DAV = "Dav";

    /** The HTTP {@code Depth} header name. */
    public static final String DEPTH = "Depth";

    /** The HTTP {@code Destination} header name. */
    public static final String DESTINATION = "Destination";

    /** The HTTP {@code ETag} header name. */
    public static final String ETAG = "ETag";

    /** The HTTP {@code Expect} header name. */
    public static final String EXPECT = "Expect";

    /** The HTTP {@code Expires} header name. */
    public static final String EXPIRES = "Expires";

    /** The HTTP {@code From} header name. */
    public static final String FROM = "From";

    /** The HTTP {@code Host} header name. */
    public static final String HOST = "Host";

    /** The HTTP {@code If} header name. */
    public static final String IF = "If";

    /** The HTTP {@code If-Match} header name. */
    public static final String IF_MATCH = "If-Match";

    /** The HTTP {@code If-Modified-Since} header name. */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /** The HTTP {@code If-None-Match} header name. */
    public static final String IF_NONE_MATCH = "If-None-Match";

    /** The HTTP {@code If-Range} header name. */
    public static final String IF_RANGE = "If-Range";

    /** The HTTP {@code If-Unmodified-Since} header name. */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /** The HTTP {@code Last-Modified} header name. */
    public static final String LAST_MODIFIED = "Last-Modified";

    /** The HTTP {@code Location} header name. */
    public static final String LOCATION = "Location";

    /** The HTTP {@code Lock-Token} header name. */
    public static final String LOCK_TOKEN = "Lock-Token";

    /** The HTTP {@code Max-Forwards} header name. */
    public static final String MAX_FORWARDS = "Max-Forwards";

    /** The HTTP {@code Overwrite} header name. */
    public static final String OVERWRITE = "Overwrite";

    /** The HTTP {@code Pragma} header name. */
    public static final String PRAGMA = "Pragma";

    /** The HTTP {@code Proxy-Authenticate} header name. */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /** The HTTP {@code Proxy-Authorization} header name. */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /** The HTTP {@code Range} header name. */
    public static final String RANGE = "Range";

    /** The HTTP {@code Referer} header name. */
    public static final String REFERER = "Referer";

    /** The HTTP {@code Retry-After} header name. */
    public static final String RETRY_AFTER = "Retry-After";

    /** The HTTP {@code Server} header name. */
    public static final String SERVER = "Server";

    /** The HTTP {@code Status-URI} header name. */
    public static final String STATUS_URI = "Status-URI";

    /** The HTTP {@code TE} header name. */
    public static final String TE = "TE";

    /** The HTTP {@code Timeout} header name. */
    public static final String TIMEOUT = "Timeout";

    /** The HTTP {@code Trailer} header name. */
    public static final String TRAILER = "Trailer";

    /** The HTTP {@code Transfer-Encoding} header name. */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    /** The HTTP {@code Upgrade} header name. */
    public static final String UPGRADE = "Upgrade";

    /** The HTTP {@code User-Agent} header name. */
    public static final String USER_AGENT = "User-Agent";

    /** The HTTP {@code Vary} header name. */
    public static final String VARY = "Vary";

    /** The HTTP {@code Via} header name. */
    public static final String VIA = "Via";

    /** The HTTP {@code Warning} header name. */
    public static final String WARNING = "Warning";

    /** The HTTP {@code WWW-Authenticate} header name. */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    private HttpHeader() {
        throw new IllegalStateException(format("The creation of the %s class is prohibited", HttpHeader.class.getName()));
    }
}
