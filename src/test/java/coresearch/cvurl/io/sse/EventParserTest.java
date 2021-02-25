package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.utils.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class EventParserTest {

    private static final String SSE_PACKAGE = "sse/";
    private EventParser parser;
    private TestSseEventListener testSseEventListener;
    private final GenericMapper defaultMapper = MapperFactory.createDefault();

    @BeforeEach
    public void setup() {
        testSseEventListener = new TestSseEventListener();
        parser = new EventParser(testSseEventListener, defaultMapper);
    }

    @Test
    public void whenEmptyEventStream_returnEmptyList() {
        parser.parse(stringAsStream(""));

        Assertions.assertTrue(testSseEventListener.getEvents().isEmpty());
    }

    @Test
    public void whenNullEventStream_returnEmptyList() {
        parser.parse(null);

        Assertions.assertTrue(testSseEventListener.getEvents().isEmpty());
    }

    @Test
    public void whenEventContainsSingleDataLine_returnEventWithData() {
        final InputStream eventStream = stringAsStream("data:test event data\n\n");
        final String data = "test event data";

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, null, data, -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsOnlyFieldNameWithEmptyFieldValue_returnEventWithEmptyField() {
        final InputStream eventStream = stringAsStream("event:\n\n");

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, "", null, -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsOnlyFieldName_returnEventWithEmptyField() {
        final InputStream eventStream = stringAsStream("event\n\n");

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, "", null, -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsSingleDataLineValueWithLeadingSpace_returnEventWithDataStrippedLeadingSpace() {
        final InputStream eventStream = stringAsStream("data: test event data\n\n");
        final String data = "test event data";

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, null, data, -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsSingleDataLineValueWithThreeLeadingSpaces_returnEventWithDataStrippedOneLeadingSpace() {
        final InputStream eventStream = stringAsStream("data:   test event data\n\n");
        final String data = "  test event data";

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, null, data, -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsNullInIdField_returnEventWithNullId() {
        final InputStream eventStream = stringAsStream("id:\0\ndata: test\n\n");

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(new InboundServerEvent(null, null, "test", -1, defaultMapper)), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsAllFields_returnEventWithParsedFields() throws IOException {
        final InputStream eventStream = resourceAsStream("event-with-single-line-data.txt");
        final String data = "{\"symbol\":\"MSFT\",\"price\":15,\"delta\":\"2\"}";
        final InboundServerEvent expectedEvent = new InboundServerEvent("1", "stock", data, 2000, defaultMapper);

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(expectedEvent), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsUnknownField_returnEventWithSupportedFields() throws IOException {
        final String data = "{\"symbol\":\"MSFT\",\"price\":15,\"delta\":\"2\"}";
        final InboundServerEvent expectedEvent = new InboundServerEvent("1", "stock", data, 2000, defaultMapper);
        final InputStream eventStream = resourceAsStream("event-with-unknown-field.txt");

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(expectedEvent), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventContainsDataOnMultipleLines_returnEventWithConcatenatedData() throws IOException {
        final String data = "{\"symbol\":\"MSFT\",\n \"price\":15,\"delta\":\"2\"}";
        final InboundServerEvent expectedEvent = new InboundServerEvent("1", "stock", data, 2000, defaultMapper);
        final InputStream eventStream = resourceAsStream("event-with-two-line-data.txt");

        parser.parse(eventStream);

        Assertions.assertEquals(List.of(expectedEvent), testSseEventListener.getEvents());
    }

    @Test
    public void whenEventStreamContainsTwoEvents_returnListWithTwoEvents() throws IOException {
        final String firstEventData = "{\"symbol\":\"MSFT\",\"price\":15,\"delta\":\"2\"}";
        final String secondEventData = "{\"symbol\":\"MSFT\",\"price\":13,\"delta\":\"-2\"}";
        final InputStream eventSteam = resourceAsStream("two-events.txt");
        final List<InboundServerEvent> expectedEvents = List.of(
                new InboundServerEvent("1", "stock", firstEventData, 2000, defaultMapper),
                new InboundServerEvent("2", "stock", secondEventData, 2000, defaultMapper)
            );

        parser.parse(eventSteam);

        Assertions.assertEquals(expectedEvents, testSseEventListener.getEvents());
    }

    @Test
    public void whenStreamIsProcessed_assertThatStreamIsClosed() throws IOException {
        final InputStream eventSteam = resourceAsStream("two-events.txt");

        parser.parse(eventSteam);

        final IOException exception = Assertions.assertThrows(IOException.class, eventSteam::read);
        Assertions.assertEquals("Stream Closed", exception.getMessage());
    }

    private InputStream stringAsStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream resourceAsStream(String resource) throws FileNotFoundException {
        final Path path = Resources.get(SSE_PACKAGE + resource);
        return new FileInputStream(path.toFile());
    }

}