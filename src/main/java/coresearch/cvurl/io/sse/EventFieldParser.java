package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.util.StringUtil;

public enum EventFieldParser {

    EVENT("event") {
        @Override
        public void processValue(String value, InboundServerEventBuilder eventBuilder) {
            eventBuilder.name(value);
        }
    },
    DATA("data") {
        private static final String LINE_FEED = "\n";

        @Override
        public void processValue(String value, InboundServerEventBuilder eventBuilder) {
            if (eventBuilder.getData() != null)
                eventBuilder.data(eventBuilder.getData() + LINE_FEED + value);
            else
                eventBuilder.data(value);
        }
    },
    ID("id") {
        private static final String NULL_CHARACTER = "\0";

        @Override
        public void processValue(String value, InboundServerEventBuilder eventBuilder) {
            if (!value.contains(NULL_CHARACTER))
                eventBuilder.id(value);
        }
    },
    RETRY("retry") {
        private static final String DIGITS_ONLY = "^\\d*$";

        @Override
        public void processValue(String value, InboundServerEventBuilder eventBuilder) {
            if (value.matches(DIGITS_ONLY))
                eventBuilder.reconnectTime(Long.parseLong(value));
        }
    },
    COMMENT(":") {
        @Override
        protected void processValue(String value, InboundServerEventBuilder eventBuilder) {
            // ignore comments
        }
    };

    protected static final String COLON = COMMENT.getFieldName();
    protected final String fieldName;

    EventFieldParser(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void parse(String line, InboundServerEventBuilder eventBuilder) {
        if (!line.startsWith(this.fieldName)) return;

        processValue(extractValue(line), eventBuilder);
    }

    protected abstract void processValue(String value, InboundServerEventBuilder eventBuilder);

    protected String extractValue(String line) {
        final String[] parts = line.split(COLON, 2);
        return StringUtil.removeOneLeadingSpace(parts.length > 1 ? parts[1] : "");
    }
}
