package coresearch.cvurl.io.model;

import coresearch.cvurl.io.internal.configuration.RequestConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestConfigurationTest {

    @Test
    void shouldReturnValidRequestConfigurationWhenDefaultConfigurationMethodIsUsed() {
        //when
        var requestConfiguration = RequestConfiguration.defaultConfiguration();

        //then
        assertTrue(requestConfiguration.getRequestTimeout().isEmpty());
        assertFalse(requestConfiguration.isAcceptCompressed());
        assertFalse(requestConfiguration.isLogEnabled());
    }

    @Test
    void shouldChangeLogEnabledValueToTrue() {
        //given
        var requestConfiguration = RequestConfiguration.defaultConfiguration();

        //when
        requestConfiguration.setLogEnabled(true);

        //then
        assertTrue(requestConfiguration.isLogEnabled());
    }
}