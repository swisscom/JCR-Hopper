package com.swisscom.aem.tools.jcrhopper.impl;

import org.slf4j.Marker;

import com.swisscom.aem.tools.jcrhopper.File;
import com.swisscom.aem.tools.jcrhopper.LogLevel;
import com.swisscom.aem.tools.jcrhopper.RunHandler;

public class NoopRunHandler implements RunHandler {
	@Override
	public void file(File file) {
	}

	@Override
	public void log(LogLevel level, String message, Marker marker) {
	}

	@Override
	public void print(String message) {
	}
}
