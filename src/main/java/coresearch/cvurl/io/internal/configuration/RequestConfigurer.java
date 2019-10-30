package coresearch.cvurl.io.internal.configuration;

import java.time.Duration;

public interface RequestConfigurer<T> {

    T requestTimeout(Duration duration);

    T acceptCompressed(boolean acceptCompressed);

    T logEnabled(boolean logEnable);
}
