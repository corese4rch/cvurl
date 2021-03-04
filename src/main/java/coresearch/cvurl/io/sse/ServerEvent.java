package coresearch.cvurl.io.sse;

public interface ServerEvent {

    String name();
    String id();
    String data();
    long reconnectTime();
    <T> T parseData(Class<T> tClass);

}
