module coresearch.curl.io {
    exports coreserech.cvurl.io.mapper;
    exports coreserech.cvurl.io.exception;
    exports coreserech.cvurl.io.model;
    exports coreserech.cvurl.io.request;
    exports coreserech.cvurl.io.util;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
}