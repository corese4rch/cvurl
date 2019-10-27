package coresearch.cvurl.io.model;

import java.time.Duration;

public interface RequestConfigurer<T> {

    T requestTimeout(Duration duration);

    T acceptCompressed(boolean acceptCompressed);
}
