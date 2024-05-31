package org.aionys.main.commons.valiation.annotations;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

	private static final PasswordValidator INSTANCE = new PasswordValidator();

	@ParameterizedTest
	@ValueSource(strings =
			{"abc", "123", "abc123", "ABC123", "abcABC", "123ABC", "abcABC123", "ABCabc123", "ABC123abc", "53f!jF"}
	)
	public void testIsValid_shouldReturnFalse(String password) {
		assertFalse(INSTANCE.isValid(password, null));
	}

	@Test
	public void testIsValid_shouldReturnTrue() {
		var password = "12Rf@abc";
		assertTrue(INSTANCE.isValid(password, null));
	}

}
