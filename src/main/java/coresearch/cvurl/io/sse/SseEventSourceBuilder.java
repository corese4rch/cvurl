package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.request.CVurl;

import java.util.concurrent.TimeUnit;

/**
 * Builder class that is used to build the {@link SseEventSource} with possibility to override the default reconnection time
 * and {@link GenericMapper} implementation that is used to parse raw data in event.
 */
public class SseEventSourceBuilder {

    private final String url;
    private final CVurl cVurl;
    private int reconnectionTime = 500;
    private GenericMapper genericMapper = MapperFactory.createDefault();

    public SseEventSourceBuilder(String url, CVurl cVurl) {
        this.url = url;
        this.cVurl = cVurl;
    }

    /**
     * Sets the initial reconnection time. Default is 500 milliseconds.
     * @param reconnectionTime duration
     * @param timeUnit time unit of the duration
     * @return this SseEventSourceBuilder builder
     */
    public SseEventSourceBuilder withReconnectionTime(int reconnectionTime, TimeUnit timeUnit) {
        this.reconnectionTime = Long.valueOf(timeUnit.toMillis(reconnectionTime)).intValue();
        return this;
    }

    /**
     * Sets the {@link GenericMapper} implementation that will be used in the {@link ServerEvent#parseData} methods
     * @param genericMapper mapper that extends the {@link GenericMapper}
     * @return this SseEventSourceBuilder builder
     */
    public SseEventSourceBuilder withMapper(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
        return this;
    }

    /**
     * Builds the {@link SseEventSource} which can be used as SSE client.
     * @return {@link SseEventSource}
     */
    public SseEventSource build() {
        return new SseEventSourceImpl(url, cVurl, reconnectionTime, genericMapper);
    }
}
