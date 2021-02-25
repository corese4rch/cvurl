package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.MapperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SseBuildEventsListenerTest {

    private static final RuntimeException DEFAULT_EXCEPTION = new RuntimeException();
    private static final InboundServerEvent DEFAULT_EVENT = new InboundServerEvent(null, null, null, -1, MapperFactory.createDefault());
    private final Collection<Consumer<ServerEvent>> eventConsumers = new ArrayList<>();
    private final Collection<Consumer<Exception>> exceptionsConsumers = new ArrayList<>();
    private final Collection<Runnable> onCompleteCallbacks = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private SseBuildEventsListener listener;

    @BeforeEach
    public void setup() {
        listener = new SseBuildEventsListener(eventConsumers, exceptionsConsumers, onCompleteCallbacks);
    }

    @Test
    public void whenOnEventCalled_thenEventIsTransmittedToEventConsumers() {
        eventConsumers.add(this::eventConsumer);
        eventConsumers.add(this::eventConsumer);

        listener.onEvent(DEFAULT_EVENT);

        Assertions.assertEquals(2, counter.get());
    }

    @Test
    public void whenOnExceptionCalled_thenExceptionIsTransmittedToExceptionConsumers() {
        exceptionsConsumers.add(this::exceptionConsumer);
        exceptionsConsumers.add(this::exceptionConsumer);

        listener.onException(DEFAULT_EXCEPTION);

        Assertions.assertEquals(2, counter.get());
    }

    @Test
    public void whenOnCompleteCalled_allCallbacksAreCalled() {
        onCompleteCallbacks.add(this::callback);
        onCompleteCallbacks.add(this::callback);

        listener.onComplete();

        Assertions.assertEquals(2, counter.get());
    }

    private void eventConsumer(ServerEvent event) {
        if (DEFAULT_EVENT.equals(event))
            counter.incrementAndGet();
    }

    private void exceptionConsumer(Exception exception) {
        if (DEFAULT_EXCEPTION.equals(exception))
            counter.incrementAndGet();
    }

    private void callback() {
        counter.incrementAndGet();
    }
}