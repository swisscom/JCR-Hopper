package com.swisscom.aem.tools.jcrhopper.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultPortalScriptOutputTest {

	@Test
	public void testPrefix() throws Exception {
		DefaultPortalScriptOutput w = new DefaultPortalScriptOutput(null, LogLevel.INFO);

		String debug = w.createPrefix(LogLevel.DEBUG);
		String info = w.createPrefix(LogLevel.INFO);
		String error = w.createPrefix(LogLevel.ERROR);

		assertSame(debug.length(), info.length());
		assertSame(info.length(), error.length());
	}
}
