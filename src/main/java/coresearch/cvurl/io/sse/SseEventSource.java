package coresearch.cvurl.io.sse;

import java.util.function.Consumer;

public interface SseEventSource extends AutoCloseable {

    SseEventSource register(Consumer<ServerEvent> eventConsumer, Consumer<Exception> exceptionConsumer, Runnable onComplete);
    SseEventSource register(Consumer<ServerEvent> eventConsumer, Consumer<Exception> exceptionConsumer);
    SseEventSource register(Consumer<ServerEvent> eventConsumer);
    void start();

}
