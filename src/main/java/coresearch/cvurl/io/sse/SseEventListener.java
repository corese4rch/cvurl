package coresearch.cvurl.io.sse;

public interface SseEventListener {

    void onEvent(ServerEvent event);
    void onException(Exception exception);
    void onComplete();

}
