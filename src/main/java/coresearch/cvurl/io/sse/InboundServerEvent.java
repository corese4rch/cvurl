package coresearch.cvurl.io.sse;

import java.util.Objects;

public class InboundServerEvent implements ServerEvent {

    private final String id;
    private final String name;
    private final String data;
    private final int reconnectTime;

    public InboundServerEvent(String id, String name, String data, int reconnectTime) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.reconnectTime = reconnectTime;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int reconnectTime() {
        return reconnectTime;
    }

    @Override
    public String data() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InboundServerEvent that = (InboundServerEvent) o;
        return reconnectTime == that.reconnectTime
                && Objects.equals(name, that.name)
                && Objects.equals(id, that.id)
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, data, reconnectTime);
    }

    @Override
    public String toString() {
        return "InboundServerEvent{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", data=" + data +
                ", reconnectTime=" + reconnectTime +
                '}';
    }

    public static InboundServerEventBuilder newBuilder() {
        return new InboundServerEventBuilder();
    }

    public static final class InboundServerEventBuilder {
        private String id;
        private String name;
        private String data;
        private int reconnectTime = -1;

        private InboundServerEventBuilder() {
            // hide from public
        }

        public InboundServerEventBuilder id(String id) {
            this.id = id;
            return this;
        }

        public InboundServerEventBuilder name(String name) {
            this.name = name;
            return this;
        }

        public InboundServerEventBuilder data(String data) {
            this.data = data;
            return this;
        }

        public InboundServerEventBuilder reconnectTime(int reconnectTime) {
            this.reconnectTime = reconnectTime;
            return this;
        }

        public String getData() {
            return data;
        }

        public InboundServerEvent build() {
            return new InboundServerEvent(id, name, data, reconnectTime);
        }
    }
}
