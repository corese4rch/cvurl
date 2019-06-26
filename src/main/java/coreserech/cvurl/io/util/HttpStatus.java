package coreserech.cvurl.io.util;

public final class HttpStatus {

    // --- 1xx Informational ---
    /** <tt>100 Continue</tt> */
    public static final int CONTINUE = 100;
    /** <tt>101 Switching Protocols</tt> */
    public static final int SWITCHING_PROTOCOLS = 101;
    /** <tt>102 Processing</tt> */
    public static final int PROCESSING = 102;

    // --- 2xx Success ---
    /** <tt>200 OK</tt> */
    public static final int OK = 200;
    /** <tt>201 Created</tt> */
    public static final int CREATED = 201;
    /** <tt>202 Accepted</tt> */
    public static final int ACCEPTED = 202;
    /** <tt>203 Non Authoritative Information</tt> */
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;
    /** <tt>204 No Content</tt> */
    public static final int NO_CONTENT = 204;
    /** <tt>205 Reset Content</tt> */
    public static final int RESET_CONTENT = 205;
    /** <tt>206 Partial Content</tt> */
    public static final int PARTIAL_CONTENT = 206;
    /** <tt>207 Multi-Status</tt> */
    public static final int MULTI_STATUS = 207;

    // --- 3xx Redirection ---
    /** <tt>300 Mutliple Choices</tt> */
    public static final int MULTIPLE_CHOICES = 300;
    /** <tt>301 Moved Permanently</tt> */
    public static final int MOVED_PERMANENTLY = 301;
    /** <tt>302 Moved Temporarily</tt> */
    public static final int MOVED_TEMPORARILY = 302;
    /** <tt>303 See Other</tt> */
    public static final int SEE_OTHER = 303;
    /** <tt>304 Not Modified</tt> */
    public static final int NOT_MODIFIED = 304;
    /** <tt>305 Use Proxy</tt> */
    public static final int USE_PROXY = 305;
    /** <tt>307 Temporary Redirect</tt> */
    public static final int TEMPORARY_REDIRECT = 307;

    // --- 4xx Client Error ---
    /**
     * <tt>400 Bad Request</tt>
     */
    public static final int BAD_REQUEST = 400;
    /** <tt>401 Unauthorized</tt> */
    public static final int UNAUTHORIZED = 401;
    /** <tt>402 Payment Required</tt> */
    public static final int PAYMENT_REQUIRED = 402;
    /** <tt>403 Forbidden</tt> */
    public static final int FORBIDDEN = 403;
    /** <tt>404 Not Found</tt> */
    public static final int NOT_FOUND = 404;
    /** <tt>405 Method Not Allowed</tt> */
    public static final int METHOD_NOT_ALLOWED = 405;
    /** <tt>406 Not Acceptable</tt> */
    public static final int NOT_ACCEPTABLE = 406;
    /** <tt>407 Proxy Authentication Required</tt> */
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
    /** <tt>408 Request Timeout</tt> */
    public static final int REQUEST_TIMEOUT = 408;
    /** <tt>409 Conflict</tt> */
    public static final int CONFLICT = 409;
    /** <tt>410 Gone</tt> */
    public static final int GONE = 410;
    /** <tt>411 Length Required</tt> */
    public static final int LENGTH_REQUIRED = 411;
    /** <tt>412 Precondition Failed</tt> */
    public static final int PRECONDITION_FAILED = 412;
    /** <tt>413 Request Entity Too Large</tt> */
    public static final int REQUEST_TOO_LONG = 413;
    /** <tt>414 Request-URI Too Long</tt> */
    public static final int REQUEST_URI_TOO_LONG = 414;
    /** <tt>415 Unsupported Media Type</tt> */
    public static final int SUNSUPPORTED_MEDIA_TYPE = 415;
    /** <tt>416 Requested Range Not Satisfiable</tt> */
    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    /** <tt>417 Expectation Failed</tt> */
    public static final int EXPECTATION_FAILED = 417;
    /** <tt>419 Insufficient Space on Resource</tt> */
    public static final int INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    /** <tt>420 Method Failure</tt> */
    public static final int METHOD_FAILURE = 420;
    /** <tt>422 Unprocessable Entity</tt> */
    public static final int UNPROCESSABLE_ENTITY = 422;
    /** <tt>423 Locked</tt> */
    public static final int LOCKED = 423;
    /** <tt>424 Failed Dependency</tt> */
    public static final int FAILED_DEPENDENCY = 424;

    // --- 5xx Server Error ---
    /** <tt>500 Server Error</tt> */
    public static final int INTERNAL_SERVER_ERROR = 500;
    /** <tt>501 Not Implemented</tt> */
    public static final int NOT_IMPLEMENTED = 501;
    /** <tt>502 Bad Gateway</tt> */
    public static final int BAD_GATEWAY = 502;
    /** <tt>503 Service Unavailable</tt> */
    public static final int SERVICE_UNAVAILABLE = 503;
    /** <tt>504 Gateway Timeout</tt> */
    public static final int GATEWAY_TIMEOUT = 504;
    /** <tt>505 HTTP Version Not Supported</tt> */
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
    /** <tt>507 Insufficient Storage</tt> */
    public static final int INSUFFICIENT_STORAGE = 507;
}
