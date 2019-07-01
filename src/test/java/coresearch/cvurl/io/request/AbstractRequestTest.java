package coresearch.cvurl.io.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import coresearch.cvurl.io.mapper.MapperFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class AbstractRequestTest {

    protected static String URL_PATTERN = "http://localhost:%d%s";
    protected static String TEST_ENDPOINT = "/test/endpoint";
    protected static String TEST_TOKEN = "test-token";
    protected static int PORT = 8080;
    @Rule
    public WireMockClassRule wiremock = new WireMockClassRule(WireMockConfiguration.options().port(PORT));
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    protected ObjectMapper mapper;
    protected CVurl cvurl;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        cvurl = new CVurl(MapperFactory.from(mapper));
    }
}
