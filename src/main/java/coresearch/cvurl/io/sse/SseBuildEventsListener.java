package coresearch.cvurl.io.sse;

import java.util.Collection;
import java.util.function.Consumer;

class SseBuildEventsListener implements SseEventListener {

    private final Collection<Consumer<ServerEvent>> eventConsumers;
    private final Collection<Consumer<Exception>> exceptionConsumers;
    private final Collection<Runnable> onCompleteCallbacks;

    SseBuildEventsListener(
            Collection<Consumer<ServerEvent>> eventConsumers,
            Collection<Consumer<Exception>> exceptionConsumers,
            Collection<Runnable> onCompleteCallbacks
    ) {
        this.eventConsumers = eventConsumers;
        this.exceptionConsumers = exceptionConsumers;
        this.onCompleteCallbacks = onCompleteCallbacks;
    }

    @Override
    public void onEvent(ServerEvent event) {
        for (Consumer<ServerEvent> eventConsumer : eventConsumers) {
            eventConsumer.accept(event);
        }
    }

    @Override
    public void onException(Exception exception) {
        for (Consumer<Exception> exceptionConsumer : exceptionConsumers) {
            exceptionConsumer.accept(exception);
        }
    }

    @Override
    public void onComplete() {
        for (Runnable completeCallback : onCompleteCallbacks) {
            completeCallback.run();
        }
    }
}
