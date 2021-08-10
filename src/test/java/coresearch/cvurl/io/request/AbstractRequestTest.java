package coresearch.cvurl.io.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import coresearch.cvurl.io.mapper.MapperFactory;
import coresearch.cvurl.io.model.CVurlConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractRequestTest {

    protected static final String URL_PATTERN = "http://localhost:%d%s";
    protected static final String TEST_ENDPOINT = "/test/endpoint";
    protected static final String TEST_TOKEN = "test-token";
    protected static final int PORT = 8181;

    protected WireMockServer wireMockServer;
    protected ObjectMapper mapper;
    protected CVurl cVurl;

    @BeforeEach
    public void setUp() {
        this.wireMockServer = new WireMockServer(PORT);

        WireMock.configureFor("localhost", PORT);

        this.wireMockServer.start();

        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.cVurl = new CVurl(CVurlConfig.builder().genericMapper(MapperFactory.from(mapper)).build());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }
}
