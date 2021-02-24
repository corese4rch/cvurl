package coresearch.cvurl.io.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class EventParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventParser.class);
    private static final String ID = "id";
    private static final String EVENT = "event";
    private static final String RETRY = "retry";
    private static final String DATA = "data";
    private static final String COLON = ":";
    private static final String LINE_FEED = "\n";
    private static final String NULL = "\0";
    private static final String DIGITS_ONLY = "^\\d*$";

    private final SseEventListener listener;

    EventParser(SseEventListener listener) {
        this.listener = listener;
    }

    public void parse(InputStream eventStream) {
        if (eventStream == null)
            return;

        String line;
        InboundServerEvent.InboundServerEventBuilder builder = InboundServerEvent.newBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(eventStream, StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    listener.onEvent(builder.build());
                    builder = InboundServerEvent.newBuilder();
                    continue;
                }

                if (line.startsWith(COLON))
                    continue;

                final String[] parts = line.split(COLON, 2);
                final String field = parts[0];
                final String value = removeOneLeadingSpace(parts.length > 1 ? parts[1] : "");
                if (field.equals(EVENT))
                    builder.name(value);

                if (field.equals(DATA)) {
                    if (builder.getData() != null)
                        builder.data(builder.getData() + LINE_FEED + value);
                    else
                        builder.data(value);
                }

                if (field.equals(ID) && !value.contains(NULL))
                    builder.id(value);

                if (field.equals(RETRY) && value.matches(DIGITS_ONLY))
                    builder.reconnectTime(Integer.parseInt(value));

            }

            listener.onComplete();
        } catch(IOException e) {
            LOGGER.error("Exception during processing the response body stream", e);
            listener.onException(e);
        }
    }

    private String removeOneLeadingSpace(String input) {
        if (input == null)
            return null;

        if (input.startsWith(" "))
            return input.substring(1);

        return input;
    }
}
