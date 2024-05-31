package org.aionys.main.commons.valiation.annotations;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NullOrNotBlankValidatorTest {

    private static final NullOrNotBlankValidator INSTANCE = new NullOrNotBlankValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void testIsValid_ShouldReturnFalse_WhenValueIsBlank(String value) {
        assertFalse(INSTANCE.isValid(value, null));
    }

    @ParameterizedTest
    @CsvSource(value = {"a", "null"}, nullValues = {"null"})
    void testIsValid_ShouldReturnTrue_WhenValueIsNotBlank(String value) {
        assertTrue(INSTANCE.isValid(value, null));
    }

}
