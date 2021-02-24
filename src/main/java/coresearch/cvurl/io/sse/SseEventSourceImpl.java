package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpStatus;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.exception.RequestExecutionException;
import coresearch.cvurl.io.model.Response;
import coresearch.cvurl.io.request.CVurl;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

class SseEventSourceImpl implements SseEventSource {

    private static final String LAST_EVENT_ID_HEADER = "Last-Event-ID";
    private static final String CACHE_NO_STORE = "no-store";

    private final Collection<Consumer<ServerEvent>> eventConsumers = new CopyOnWriteArrayList<>();
    private final Collection<Consumer<Exception>> exceptionConsumers = new CopyOnWriteArrayList<>();
    private final Collection<Runnable> onCompleteCallbacks = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final SseEventListener listener = new SseBuildEventsListener(eventConsumers, exceptionConsumers, onCompleteCallbacks);
    private final EventParser eventParser = new EventParser(listener);

    private final AtomicReference<SseEventSourceState> state = new AtomicReference<>(SseEventSourceState.CONNECTING);
    private final String url;
    private final CVurl cVurl;
    private volatile int reconnectionTime;
    private volatile String lastEventId = "";

    SseEventSourceImpl(String url, CVurl cVurl, int reconnectionTime) {
        this.url = url;
        this.cVurl = cVurl;
        this.reconnectionTime = reconnectionTime;

        eventConsumers.add(this::updateReconnectionTime);
        eventConsumers.add(this::updateLastEventId);
    }

    @Override
    public SseEventSource register(Consumer<ServerEvent> eventConsumer, Consumer<Exception> exceptionConsumer, Runnable onComplete) {
        if (eventConsumer != null)
            eventConsumers.add(eventConsumer);

        if (exceptionConsumer != null)
            exceptionConsumers.add(exceptionConsumer);

        if (onComplete != null)
            onCompleteCallbacks.add(onComplete);

        return this;
    }

    @Override
    public SseEventSource register(Consumer<ServerEvent> serverEventConsumer, Consumer<Exception> exceptionConsumer) {
        this.register(serverEventConsumer, exceptionConsumer, null);
        return this;
    }

    @Override
    public SseEventSource register(Consumer<ServerEvent> serverEventConsumer) {
        this.register(serverEventConsumer, null, null);
        return this;
    }

    @Override
    public void close() {
        try {
            state.set(SseEventSourceState.CLOSED);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        executorService.submit(this::tryMakeRequest);
    }

    private void tryMakeRequest() {
        try {
            makeRequest();
        } catch(Exception e) {
            listener.onException(e);
            failTheConnection();
        }
    }

    private void makeRequest() {
        final Response<InputStream> response = cVurl.get(url)
                .headers(prepareHeadersMap())
                .asStream()
                .orElseThrow(() -> new RequestExecutionException("Unknown exception", null));

        final Optional<String> contentType = response.getHeaderValue(HttpHeader.CONTENT_TYPE);
        if (isStatusNotOK(response.status()) || isNotSupportedContentType(contentType.orElse(""))) {
            failTheConnection();
            return;
        }

        announceTheConnection();
        eventParser.parse(response.getBody());

        if (isNotClosed()) {
            reestablishTheConnection();
        } else {
            failTheConnection();
        }
    }

    private boolean isNotClosed() {
        return state.compareAndSet(SseEventSourceState.OPEN, SseEventSourceState.CONNECTING) &&
                !executorService.isShutdown() &&
                !executorService.isTerminated();
    }

    private Map<String, String> prepareHeadersMap() {
        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.ACCEPT, MIMEType.TEXT_EVENT_STREAM);
        headers.put(HttpHeader.CACHE_CONTROL, CACHE_NO_STORE);

        if (!lastEventId.isEmpty())
            headers.put(LAST_EVENT_ID_HEADER, lastEventId);

        return headers;
    }

    private boolean isStatusNotOK(int status) {
        return status != HttpStatus.OK;
    }

    private boolean isNotSupportedContentType(String contentTypeOptional) {
        return !contentTypeOptional.equals(MIMEType.TEXT_EVENT_STREAM);
    }

    private void updateReconnectionTime(ServerEvent event) {
            final int eventReconnectionTime = event.reconnectTime();
            if (eventReconnectionTime > 0)
                this.reconnectionTime = eventReconnectionTime;
    }

    private void updateLastEventId(ServerEvent event) {
        lastEventId = event.id() != null ? event.id() : "";
    }

    private void announceTheConnection() {
        state.compareAndSet(SseEventSourceState.CONNECTING, SseEventSourceState.OPEN);
    }

    private void reestablishTheConnection() {
        executorService.schedule(this::tryMakeRequest, reconnectionTime, TimeUnit.MILLISECONDS);
    }

    private void failTheConnection() {
        close();
    }

}
