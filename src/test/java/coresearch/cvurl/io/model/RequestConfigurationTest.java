package coresearch.cvurl.io.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestConfigurationTest {

    @Test
    public void defaultConfigurationTest() {
        //when
        var requestConfiguration = RequestConfiguration.defaultConfiguration();

        //then
        assertTrue(requestConfiguration.getRequestTimeout().isEmpty());
        assertFalse(requestConfiguration.isAcceptCompressed());
        assertFalse(requestConfiguration.isLogEnabled());
    }

    @Test
    public void logEnabledIsMutableTest() {
        //given
        var requestConfiguration = RequestConfiguration.defaultConfiguration();

        //when
        requestConfiguration.setLogEnabled(true);

        //then
        assertTrue(requestConfiguration.isLogEnabled());
    }
}