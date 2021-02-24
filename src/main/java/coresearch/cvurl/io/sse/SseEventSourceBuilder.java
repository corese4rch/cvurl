package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.request.CVurl;

import java.util.concurrent.TimeUnit;

public class SseEventSourceBuilder {

    private int reconnectionTime = 500;
    private final String url;
    private final CVurl cVurl;

    public SseEventSourceBuilder(String url, CVurl cVurl) {
        this.url = url;
        this.cVurl = cVurl;
    }

    public SseEventSourceBuilder initialReconnectionTime(int reconnectionTime, TimeUnit timeUnit) {
        this.reconnectionTime = Long.valueOf(timeUnit.toMillis(reconnectionTime)).intValue();
        return this;
    }

    public SseEventSource build() {
        return new SseEventSourceImpl(url, cVurl, reconnectionTime);
    }
}
