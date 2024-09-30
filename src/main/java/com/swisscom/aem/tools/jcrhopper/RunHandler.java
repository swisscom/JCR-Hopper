package com.swisscom.aem.tools.jcrhopper;

import org.slf4j.Marker;

public interface RunHandler {
	void file(File file);

	void log(LogLevel level, String message, Marker marker);

	default void log(LogLevel level, String message) {
		log(level, message, null);
	}

	void print(String message);
}
