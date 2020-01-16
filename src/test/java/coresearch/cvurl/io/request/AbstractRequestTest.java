package coresearch.cvurl.io.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import coresearch.cvurl.io.mapper.MapperFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractRequestTest {

    protected static final String URL_PATTERN = "http://localhost:%d%s";
    protected static final String TEST_ENDPOINT = "/test/endpoint";
    protected static final String TEST_TOKEN = "test-token";
    protected static final int PORT = 8080;

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
