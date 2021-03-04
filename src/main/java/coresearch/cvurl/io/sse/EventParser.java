package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.GenericMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class EventParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventParser.class);

    private final SseEventListener listener;
    private final GenericMapper genericMapper;

    EventParser(SseEventListener listener, GenericMapper genericMapper) {
        this.listener = listener;
        this.genericMapper = genericMapper;
    }

    public void parse(InputStream eventStream) {
        if (eventStream == null)
            return;

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(eventStream, StandardCharsets.UTF_8))) {
            processReader(reader);
        } catch(IOException e) {
            LOGGER.error("Exception during processing the response body stream", e);
            listener.onException(e);
        }
    }

    private void processReader(BufferedReader reader) throws IOException {
        String line;
        InboundServerEventBuilder builder = InboundServerEvent.newBuilder(genericMapper);
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                listener.onEvent(builder.build());
                builder = InboundServerEvent.newBuilder(genericMapper);
                continue;
            }

            for (EventFieldParser fieldParser : EventFieldParser.values()) {
                fieldParser.parse(line, builder);
            }
        }

        listener.onComplete();
    }
}
