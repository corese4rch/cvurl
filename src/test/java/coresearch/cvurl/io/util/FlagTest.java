package coresearch.cvurl.io.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlagTest {

    @Test
    public void ofShouldReturnProperFlagTest() {
        assertEquals(FeatureFlag.of(true), FeatureFlag.ENABLED);
        assertEquals(FeatureFlag.of(false), FeatureFlag.DISABLED);
    }

    @Test
    public void enabledFlagShouldRunGivenLambdaTest() {
        //given
        var enabled = FeatureFlag.ENABLED;
        boolean[] isInvoked = {false};

        //when
        enabled.let(() -> isInvoked[0] = true);

        //then
        assertTrue(isInvoked[0]);
    }

    @Test
    public void disabledFlagShouldNotRunGivenLambdaTest() {
        //given
        var disabled = FeatureFlag.DISABLED;
        boolean[] isInvoked = {false};

        //when
        disabled.let(() -> isInvoked[0] = true);

        //then
        assertFalse(isInvoked[0]);
    }
}