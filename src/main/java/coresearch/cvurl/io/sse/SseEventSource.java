package coresearch.cvurl.io.sse;

import java.util.function.Consumer;

/**
 * Interface for SSE client.
 * Use {@link SseEventSource#register(Consumer)} method to subscribe to events.
 * Use {@link SseEventSource#start()} method to connect to remote server and process events if any.
 * Note that this interface extends the AutoCloseable interface and it should be closed via {@link AutoCloseable#close()}.
 */
public interface SseEventSource extends AutoCloseable {

    /**
     * Registers consumers for server sent events and Exceptions.
     * Also registers onComplete callback which is then gets called when event stream end is encountered.
     * @param eventConsumer consumer for server sent events, called whenever new event is received
     * @param exceptionConsumer consumer for Exceptions, called whenever exception is thrown during event stream processing
     * @param onComplete callback that gets called when end of event stream is encountered
     * @return this SseEventSource
     */
    SseEventSource register(Consumer<ServerEvent> eventConsumer, Consumer<Exception> exceptionConsumer, Runnable onComplete);

    /**
     * Registers consumers for server sent events and Exceptions.
     * @param eventConsumer consumer for server sent events, called whenever new event is received
     * @param exceptionConsumer consumer for Exceptions, called whenever exception is thrown during event stream processing
     * @return this SseEventSource
     */
    SseEventSource register(Consumer<ServerEvent> eventConsumer, Consumer<Exception> exceptionConsumer);

    /**
     * Registers consumers for server sent events.
     * @param eventConsumer consumer for server sent events, called whenever new event is received
     * @return this SseEventSource
     */
    SseEventSource register(Consumer<ServerEvent> eventConsumer);

    /**
     * Initiates the connection to the remote server and starts processing of event stream if any.
     */
    void start();

}
