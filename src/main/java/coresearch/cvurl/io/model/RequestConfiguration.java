package coresearch.cvurl.io.model;

import java.time.Duration;
import java.util.Optional;

public class RequestConfiguration {
    private final Duration requestTimeout;
    private final boolean acceptCompressed;
    private boolean isLogEnabled;

    public RequestConfiguration() {
        this.requestTimeout = null;
        this.acceptCompressed = false;
        this.isLogEnabled = false;
    }

    private RequestConfiguration(Duration requestTimeout, boolean acceptCompressed, boolean isLogEnabled) {
        this.requestTimeout = requestTimeout;
        this.acceptCompressed = acceptCompressed;
        this.isLogEnabled = isLogEnabled;
    }

    public Builder preconfiguredBuilder() {
        return builder()
                .requestTimeout(requestTimeout)
                .acceptCompressed(acceptCompressed)
                .isLogEnabled(isLogEnabled);
    }

    public Optional<Duration> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    public boolean isAcceptCompressed() {
        return acceptCompressed;
    }

    public boolean getIsLogEnabled() {
        return isLogEnabled;
    }

    public void setIsLogEnabled(boolean enabled) {
        this.isLogEnabled = enabled;
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
        private boolean isLogEnabled;

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

        @Override
        public Builder isLogEnabled(boolean isLogEnabled) {
            this.isLogEnabled = isLogEnabled;
            return this;
        }

        public RequestConfiguration build() {
            return new RequestConfiguration(timeout, acceptCompressed, isLogEnabled);
        }
    }
}
