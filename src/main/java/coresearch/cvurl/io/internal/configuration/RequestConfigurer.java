package coresearch.cvurl.io.internal.configuration;

import java.time.Duration;

/**
 * The interface defines a contract for the {@link RequestConfiguration} class building.
 *
 * @since 1.2
 * @param <T> - the builder class type
 */
public interface RequestConfigurer<T> {

    /**
     * Sets the request timeout.
     *
     * @param duration - the request timeout
     * @return the builder
     */
    T requestTimeout(Duration duration);

    /**
     * Sets whether the client should accept the compressed response body or not.
     *
     * @param acceptCompressed - whether to accept compressed or not
     * @return the builder
     */
    T acceptCompressed(boolean acceptCompressed);

    /**
     * Sets whether the client should enable logging or not.
     *
     * @param logEnabled - whether to enable logging or not
     * @return the builder
     */
    T logEnabled(boolean logEnabled);
}
