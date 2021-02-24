package coresearch.cvurl.io.sse;

import java.util.ArrayList;
import java.util.List;

public class TestSseEventListener implements SseEventListener {

    private final List<ServerEvent> events = new ArrayList<>();
    private final List<Exception> exceptions = new ArrayList<>();

    @Override
    public void onEvent(ServerEvent event) {
        events.add(event);
    }

    @Override
    public void onException(Exception exception) {
        exceptions.add(exception);
    }

    @Override
    public void onComplete() {

    }

    public List<ServerEvent> getEvents() {
        return events;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }
}
