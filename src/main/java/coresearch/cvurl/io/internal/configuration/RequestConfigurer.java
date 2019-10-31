package coresearch.cvurl.io.internal.configuration;

import java.time.Duration;

/**
 * Interface that defines protocol for {@link RequestConfiguration} building.
 *
 * @param <T>
 */
public interface RequestConfigurer<T> {

    /**
     * Sets requestTimeout of request.
     *
     * @param duration request timeout
     * @return this builder
     */
    T requestTimeout(Duration duration);

    /**
     * Sets whether client should accept compressed response body.
     *
     * @param acceptCompressed whether accept compressed
     * @return this builder
     */
    T acceptCompressed(boolean acceptCompressed);

    /**
     * Sets flag that defines if request body and url should be logged with level INFO.
     *
     * @param logEnabled whether accept compressed
     * @return this builder
     */
    T logEnabled(boolean logEnabled);
}
