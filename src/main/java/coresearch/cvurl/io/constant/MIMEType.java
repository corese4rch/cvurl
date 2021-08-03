package coresearch.cvurl.io.constant;

import static java.lang.String.format;

/**
 * Contains constant definitions for the HTTP media types.
 *
 * @since 0.9
 */
public final class MIMEType {

    /** The HTTP {@code application/json} media type. */
    public static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    /** The HTTP {@code application/octet-stream} media type. */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /** The HTTP {@code application/javascript} media type. */
    public static final String APPLICATION_JS = "application/javascript";

    /** The HTTP {@code application/x-www-form-urlencoded} media type. */
    public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";

    /** The HTTP {@code application/xml} media type. */
    public static final String APPLICATION_XML = "application/xml";

    /** The HTTP {@code application/zip} media type. */
    public static final String APPLICATION_ZIP = "application/zip";

    /** The HTTP {@code application/pdf} media type. */
    public static final String APPLICATION_PDF = "application/pdf";

    /** The HTTP {@code application/sql} media type. */
    public static final String APPLICATION_SQL = "application/sql";

    /** The HTTP {@code multipart/form-data} media type. */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /** The HTTP {@code text/css} media type. */
    public static final String TEXT_CSS = "text/css";

    /** The HTTP {@code text/html} media type. */
    public static final String TEXT_HTML = "text/html";

    /** The HTTP {@code text/csv} media type. */
    public static final String TEXT_CSV = "text/csv";

    /** The HTTP {@code text/plain} media type. */
    public static final String TEXT_PLAIN = "text/plain";

    /** The HTTP {@code text/xml} media type. */
    public static final String TEXT_XML = "text/xml";

    private MIMEType(){
        throw new IllegalStateException(format("The creation of the %s class is prohibited", MIMEType.class.getName()));
    }
}
