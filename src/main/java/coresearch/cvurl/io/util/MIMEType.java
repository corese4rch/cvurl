package coresearch.cvurl.io.util;

import static java.lang.String.format;

public final class MIMEType {

    public static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    public static final String APPLICATION_OCTET_STRING = "application/octet-stream";

    public static final String APPLICATION_JS = "application/javascript";

    public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";

    public static final String APPLICATION_XML = "application/xml";

    public static final String APPLICATION_ZIP = "application/zip";

    public static final String APPLICATION_PDF = "application/pdf";

    public static final String APPLICATION_SQL = "application/sql";

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static final String TEXT_CSS = "text/css";

    public static final String TEXT_HTML = "text/html";

    public static final String TEXT_CSV = "text/csv";

    public static final String TEXT_PLAIN = "text/plain";

    public static final String TEXT_XML = "text/xml";

    private MIMEType(){
        throw new IllegalStateException(format("Creating of class %s is forbidden", MIMEType.class.getName()));
    }
}
