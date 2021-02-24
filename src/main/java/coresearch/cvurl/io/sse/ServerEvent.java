package coresearch.cvurl.io.sse;

public interface ServerEvent {

    String name();
    String id();
    String data();
    int reconnectTime();

}
