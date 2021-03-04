package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.mapper.GenericMapper;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.utils.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class EventParserTest {

    private static final String SSE_PACKAGE = "sse/";
    private static final GenericMapper defaultMapper = MapperFactory.createDefault();
    private EventParser parser;
    private TestSseEventListener testSseEventListener;

    @BeforeEach
    public void setup() {
        testSseEventListener = new TestSseEventListener();
        parser = new EventParser(testSseEventListener, defaultMapper);
    }

    @Test
    void whenNullEventStream_returnEmptyList() {
        parser.parse(null);

        Assertions.assertTrue(testSseEventListener.getEvents().isEmpty());
    }

    @Test
    void whenStreamIsProcessed_assertThatStreamIsClosed() throws IOException {
        final InputStream eventSteam = resourceAsStream("two-events.txt");

        parser.parse(eventSteam);

        final IOException exception = Assertions.assertThrows(IOException.class, eventSteam::read);
        Assertions.assertEquals("Stream Closed", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("testEventParsingArgumentsSource")
    void testEventParsing(String eventStream, List<ServerEvent> expectedEvents) {
        parser.parse(stringAsStream(eventStream));

        Assertions.assertEquals(expectedEvents, testSseEventListener.getEvents());
    }

    private static Stream<Arguments> testEventParsingArgumentsSource() {
        return Stream.of(
            Arguments.of("", List.of()),
            Arguments.of("data:test event data\n\n", singleEventWithData("test event data")),
            Arguments.of("event:\n\n", singleEventWithName("")),
            Arguments.of("data: test event data\n\n", singleEventWithData("test event data")),
            Arguments.of("data:   test event data\n\n", singleEventWithData("  test event data")),
            Arguments.of("id:\0\ndata: test\n\n", singleEventWithData("test")),
            Arguments.of(resourceAsString("event-with-single-line-data.txt"), List.of(firstExpectedStockEvent())),
            Arguments.of(resourceAsString("event-with-unknown-field.txt"), List.of(firstExpectedStockEvent())),
            Arguments.of(resourceAsString("event-with-two-line-data.txt"), List.of(expectedStockEventWithLineFeedInData())),
            Arguments.of(resourceAsString("two-events.txt"), List.of(
                    firstExpectedStockEvent(),
                    makeEvent("2", "stock", "{\"symbol\":\"MSFT\",\"price\":13,\"delta\":\"-2\"}", 2000)
            ))
        );
    }

    private static List<ServerEvent> singleEventWithData(String data) {
        return List.of(new InboundServerEvent(null, null, data, -1, defaultMapper));
    }

    private static List<ServerEvent> singleEventWithName(String name) {
        return List.of(new InboundServerEvent(null, name, null, -1, defaultMapper));
    }

    private static ServerEvent makeEvent(String id, String name, String data, long reconnectTime) {
        return new InboundServerEvent(id, name, data, reconnectTime, defaultMapper);
    }

    private static ServerEvent firstExpectedStockEvent() {
        return makeEvent("1", "stock", "{\"symbol\":\"MSFT\",\"price\":15,\"delta\":\"2\"}", 2000);
    }

    private static ServerEvent expectedStockEventWithLineFeedInData() {
        return makeEvent("1", "stock", "{\"symbol\":\"MSFT\",\n \"price\":15,\"delta\":\"2\"}", 2000);
    }

    private InputStream stringAsStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    private static String resourceAsString(String resource) {
        try {
            final Path path = Resources.get(SSE_PACKAGE + resource);
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream resourceAsStream(String resource) throws FileNotFoundException {
        final Path path = Resources.get(SSE_PACKAGE + resource);
        return new FileInputStream(path.toFile());
    }

}