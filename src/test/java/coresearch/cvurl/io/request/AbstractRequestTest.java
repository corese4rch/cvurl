package coresearch.cvurl.io.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import coresearch.cvurl.io.mapper.MapperFactory;
import org.junit.jupiter.api.*;

public abstract class AbstractRequestTest {

    protected static String URL_PATTERN = "http://localhost:%d%s";
    protected static String TEST_ENDPOINT = "/test/endpoint";
    protected static String TEST_TOKEN = "test-token";
    protected static int PORT = 8080;

    protected WireMockServer wiremock;
    protected ObjectMapper mapper;
    protected CVurl cvurl;

    @BeforeEach
    public void setUp() {
        wiremock = new WireMockServer(PORT);
        wiremock.start();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        cvurl = new CVurl(MapperFactory.from(mapper));
    }

    @AfterEach
    public void tearDown() {
        wiremock.stop();
    }
}
