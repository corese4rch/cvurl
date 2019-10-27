package coresearch.cvurl.io.model;

import java.time.Duration;
import java.util.Optional;

public class RequestConfiguration {
    private final Duration requestTimeout;
    private final boolean acceptCompressed;

    public RequestConfiguration() {
        this.requestTimeout = null;
        this.acceptCompressed = false;
    }

    private RequestConfiguration(Duration requestTimeout, boolean acceptCompressed) {
        this.requestTimeout = requestTimeout;
        this.acceptCompressed = acceptCompressed;
    }

    public Builder preconfiguredBuilder() {
        return builder()
                .requestTimeout(requestTimeout)
                .acceptCompressed(acceptCompressed);
    }

    public Optional<Duration> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    public boolean isAcceptCompressed() {
        return acceptCompressed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RequestConfiguration defaultConfiguration() {
        return new RequestConfiguration();
    }

    public static class Builder implements RequestConfigurer<Builder> {
        private Duration timeout;
        private boolean acceptCompressed;

        @Override
        public Builder requestTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public Builder acceptCompressed(boolean acceptCompressed) {
            this.acceptCompressed = acceptCompressed;
            return this;
        }

        public RequestConfiguration build() {
            return new RequestConfiguration(timeout, acceptCompressed);
        }
    }
}
