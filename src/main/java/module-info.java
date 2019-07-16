module coresearch.cvurl.io {
    exports coresearch.cvurl.io.mapper;
    exports coresearch.cvurl.io.exception;
    exports coresearch.cvurl.io.model;
    exports coresearch.cvurl.io.request;
    exports coresearch.cvurl.io.util;
    exports coresearch.cvurl.io.constant;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
}