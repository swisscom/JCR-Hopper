package com.swisscom.aem.tools.jcrhopper.impl;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogLevelTest {

	@Test
	public void testFromStringNull() throws Exception {
		assertEquals(LogLevel.INFO, LogLevel.fromString(null));
	}

	@Test
	public void testFromUnknownString() throws Exception {
		assertEquals(LogLevel.INFO, LogLevel.fromString("lala"));
	}

	@Test
	public void testLowerCase() throws Exception {
		assertEquals(LogLevel.DEBUG, LogLevel.fromString("debug"));
	}

	@Test
	public void testMixedCase() throws Exception {
		assertEquals(LogLevel.DEBUG, LogLevel.fromString("deBug"));
	}

	@Test
	public void testUpperCase() throws Exception {
		assertEquals(LogLevel.ERROR, LogLevel.fromString("ERROR"));
	}

	@Test
	public void testAll() throws Exception {
		for (LogLevel level : LogLevel.values()) {
			assertEquals(level, LogLevel.fromString(level.toString().toLowerCase()));
		}
	}
}
