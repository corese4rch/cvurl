package coresearch.cvurl.io.model;

import java.time.Duration;
import java.util.Optional;

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

    public Builder preconfiguredBuilder() {
        return builder()
                .requestTimeout(requestTimeout)
                .acceptCompressed(acceptCompressed)
                .logEnabled(logEnabled);
    }

    public Optional<Duration> getRequestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    public boolean isAcceptCompressed() {
        return acceptCompressed;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean enabled) {
        this.logEnabled = enabled;
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
        private boolean logEnabled;

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
        public Builder logEnabled(boolean logEnable) {
            this.logEnabled = logEnable;
            return this;
        }

        public RequestConfiguration build() {
            return new RequestConfiguration(timeout, acceptCompressed, logEnabled);
        }
    }
}
