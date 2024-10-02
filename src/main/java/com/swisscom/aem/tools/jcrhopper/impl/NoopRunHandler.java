package com.swisscom.aem.tools.jcrhopper.impl;

import org.slf4j.Marker;

import com.swisscom.aem.tools.jcrhopper.config.File;
import com.swisscom.aem.tools.jcrhopper.config.LogLevel;
import com.swisscom.aem.tools.jcrhopper.config.RunHandler;

public class NoopRunHandler implements RunHandler {
	@Override
	public void file(File file) {
		// No-op
	}

	@Override
	public void log(LogLevel level, String message, Marker marker) {
		// No-op
	}

	@Override
	public void print(String message) {
		// No-op
	}
}
