package coresearch.cvurl.io.internal.configuration;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration properties for the {@link coresearch.cvurl.io.request.RequestBuilder}
 * and {@link coresearch.cvurl.io.request.CVurlRequest} classes.
 *
 * @since 1.2
 */
public class RequestConfiguration {

    private final Duration requestTimeout;
    private final boolean acceptCompressed;
    private boolean logEnabled;

    public RequestConfiguration() {
        this.requestTimeout = null;
        this.acceptCompressed = false;
        this.logEnabled = false;
    }

    private RequestConfiguration(Duration requestTimeout, boolean acceptCompressed, boolean logEnabled) {
        this.requestTimeout = requestTimeout;
        this.acceptCompressed = acceptCompressed;
        this.logEnabled = logEnabled;
    }

    /**
     * Returns a preconfigured builder for the {@link RequestConfiguration} class.
     * @return the builder
     */
    public Builder preconfiguredBuilder() {
        return builder()
                .requestTimeout(requestTimeout)
                .acceptCompressed(acceptCompressed)
                .logEnabled(logEnabled);
    }

    /**
     * Returns the {@code requestTimeout} value.
     */
    public Optional<Duration> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    /**
     * Returns the {@code acceptCompressed} value.
     */
    public boolean isAcceptCompressed() {
        return acceptCompressed;
    }

    /**
     * Returns the {@code logEnabled} value.
     */
    public boolean isLogEnabled() {
        return logEnabled;
    }

    /**
     * Sets the value of the {@code logEnabled} field.
     * @param enabled - the value of the logEnabled field
     */
    public void setLogEnabled(boolean enabled) {
        this.logEnabled = enabled;
    }

    /**
     * Returns a builder for the {@link RequestConfiguration} class.
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a default {@link RequestConfiguration} instance.
     * @return an instance of the {@link RequestConfiguration} class with default values
     */
    public static RequestConfiguration defaultConfiguration() {
        return new RequestConfiguration();
    }

    /**
     * A mutable builder for the {@link RequestConfiguration} class.
     *
     * @since 1.2
     */
    public static class Builder implements RequestConfigurer<Builder> {

        private Duration timeout;
        private boolean acceptCompressed;
        private boolean logEnabled;

        /**
         * Sets the value of the {@code timeout} field.
         * @param timeout - the value of the timeout field
         * @return the builder
         */
        @Override
        public Builder requestTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the value of the {@code acceptCompressed} field.
         * @param acceptCompressed - the value of the acceptCompressed field
         * @return the builder
         */
        @Override
        public Builder acceptCompressed(boolean acceptCompressed) {
            this.acceptCompressed = acceptCompressed;
            return this;
        }

        /**
         * Sets the value of the {@code logEnabled} field.
         * @param logEnabled - the value of the logEnabled field
         * @return the builder
         */
        @Override
        public Builder logEnabled(boolean logEnabled) {
            this.logEnabled = logEnabled;
            return this;
        }

        /**
         * Builds the request configuration.
         * @return an instance of the {@link RequestConfiguration} class
         */
        public RequestConfiguration build() {
            return new RequestConfiguration(timeout, acceptCompressed, logEnabled);
        }
    }
}
