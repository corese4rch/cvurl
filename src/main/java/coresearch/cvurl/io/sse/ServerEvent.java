package coresearch.cvurl.io.sse;

public interface ServerEvent {

    String name();
    String id();
    String data();
    int reconnectTime();
    <T> T parseData(Class<T> tClass);

}
