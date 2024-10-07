package com.swisscom.aem.tools.jcrhopper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import org.junit.jupiter.api.Test;

public class LogLevelTest {

	@Test
	public void fromString_null() {
		assertEquals(LogLevel.INFO, LogLevel.fromName(null));
	}

	@Test
	public void fromString_unknown() {
		assertEquals(LogLevel.INFO, LogLevel.fromName("lala"));
	}

	@Test
	public void fromString_lowerCase() {
		assertEquals(LogLevel.DEBUG, LogLevel.fromName("debug"));
	}

	@Test
	public void fromString_mixedCase() {
		assertEquals(LogLevel.DEBUG, LogLevel.fromName("deBug"));
	}

	@Test
	public void ufromString_pperCase() {
		assertEquals(LogLevel.ERROR, LogLevel.fromName("ERROR"));
	}

	@Test
	public void allLevels() {
		for (LogLevel level : LogLevel.values()) {
			assertEquals(level, LogLevel.fromName(level.toString().toLowerCase()));
		}
	}

	@Test
	public void shouldLogTo_trace() {
		assertTrue(LogLevel.TRACE.shouldLogTo(LogLevel.TRACE));
		assertFalse(LogLevel.TRACE.shouldLogTo(LogLevel.DEBUG));
		assertFalse(LogLevel.TRACE.shouldLogTo(LogLevel.INFO));
		assertFalse(LogLevel.TRACE.shouldLogTo(LogLevel.WARN));
		assertFalse(LogLevel.TRACE.shouldLogTo(LogLevel.ERROR));
	}

	@Test
	public void shouldLogTo_debug() {
		assertTrue(LogLevel.DEBUG.shouldLogTo(LogLevel.TRACE));
		assertTrue(LogLevel.DEBUG.shouldLogTo(LogLevel.DEBUG));
		assertFalse(LogLevel.DEBUG.shouldLogTo(LogLevel.INFO));
		assertFalse(LogLevel.DEBUG.shouldLogTo(LogLevel.WARN));
		assertFalse(LogLevel.DEBUG.shouldLogTo(LogLevel.ERROR));
	}

	@Test
	public void shouldLogTo_info() {
		assertTrue(LogLevel.INFO.shouldLogTo(LogLevel.TRACE));
		assertTrue(LogLevel.INFO.shouldLogTo(LogLevel.DEBUG));
		assertTrue(LogLevel.INFO.shouldLogTo(LogLevel.INFO));
		assertFalse(LogLevel.INFO.shouldLogTo(LogLevel.WARN));
		assertFalse(LogLevel.INFO.shouldLogTo(LogLevel.ERROR));
	}

	@Test
	public void shouldLogTo_warn() {
		assertTrue(LogLevel.WARN.shouldLogTo(LogLevel.TRACE));
		assertTrue(LogLevel.WARN.shouldLogTo(LogLevel.DEBUG));
		assertTrue(LogLevel.WARN.shouldLogTo(LogLevel.INFO));
		assertTrue(LogLevel.WARN.shouldLogTo(LogLevel.WARN));
		assertFalse(LogLevel.WARN.shouldLogTo(LogLevel.ERROR));
	}

	@Test
	public void shouldLogTo_error() {
		assertTrue(LogLevel.ERROR.shouldLogTo(LogLevel.TRACE));
		assertTrue(LogLevel.ERROR.shouldLogTo(LogLevel.DEBUG));
		assertTrue(LogLevel.ERROR.shouldLogTo(LogLevel.INFO));
		assertTrue(LogLevel.ERROR.shouldLogTo(LogLevel.WARN));
		assertTrue(LogLevel.ERROR.shouldLogTo(LogLevel.ERROR));
	}
}
