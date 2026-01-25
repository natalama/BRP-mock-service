package nl.pink.mocks.brp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BsnUtilsTest {
    @ParameterizedTest
    @ValueSource(strings = {"123456789", "111111110", "222222220", "012345679"})
    void testIsValidBsn_valid(String validBsn) {
        assertTrue(BsnUtils.isValidBsn(validBsn));
    }

    @ParameterizedTest
    @ValueSource(strings = {"000000000", "abcdefgh", "", "123", "123456782"})
    void testInvalidScenarios(String input) {
        assertFalse(BsnUtils.isValidBsn(input));
    }
}
