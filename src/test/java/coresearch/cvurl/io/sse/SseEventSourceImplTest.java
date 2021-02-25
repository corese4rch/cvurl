package coresearch.cvurl.io.sse;

import com.github.tomakehurst.wiremock.client.WireMock;
import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.HttpStatus;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.request.AbstractRequestTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SseEventSourceImplTest extends AbstractRequestTest {

    private final String TEST_URL = String.format(URL_PATTERN, PORT, TEST_ENDPOINT);

    @Test
    void whenEventIsReceived_eventConsumerIsCalled() throws Exception {
        final boolean[] wasTriggered = {false};

        wiremock.stubFor(WireMock.get(WireMock.urlEqualTo(TEST_ENDPOINT))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK)
                    .withHeader(HttpHeader.CONTENT_TYPE, MIMEType.TEXT_EVENT_STREAM)
                    .withBody("data: test event\n\n")));

        final SseEventSource source = cvurl.sse(TEST_URL).build();
        source.register(event -> wasTriggered[0] = true);

        source.start();
        source.close();

        Assertions.assertTrue(wasTriggered[0]);
    }

}