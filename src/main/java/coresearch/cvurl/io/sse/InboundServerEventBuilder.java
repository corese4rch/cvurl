package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.GenericMapper;

public class InboundServerEventBuilder {
    private String id;
    private String name;
    private String data;
    private long reconnectTime = -1;
    private final GenericMapper genericMapper;

    InboundServerEventBuilder(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
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

    public InboundServerEventBuilder reconnectTime(long reconnectTime) {
        this.reconnectTime = reconnectTime;
        return this;
    }

    public String getData() {
        return data;
    }

    public InboundServerEvent build() {
        return new InboundServerEvent(id, name, data, reconnectTime, genericMapper);
    }
}
