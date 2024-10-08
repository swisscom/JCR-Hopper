package com.swisscom.aem.tools.jcrhopper.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Log level which is used in portal scripts. Keep in mind that different levels are for different users.
 * <ul>
 * <li>DEBUG: For developers</li>
 * <li>INFO: For operation (the guys hate to read - so keep it to a minimum!)</li>
 * <li>ERROR: For errors and exception</li>
 * </ul>
 *
 */
public enum LogLevel {
	DEBUG,
	INFO,
	WARN,
	ERROR;

	public static final LogLevel DEFAULT = INFO;
	private static final Map<String, LogLevel> LEVELS = new HashMap<>();

	static {
		for (LogLevel level : LogLevel.values()) {
			LEVELS.put(level.name().toLowerCase(), level);
		}
	}

	/**
	 * Returns the LogLevel from String. If the level is null the default LogLevel is returned.
	 *
	 * @param level String log level
	 * @return LogLevel
	 */
	public static LogLevel fromString(final String level) {
		return Optional.ofNullable(level)
			.map(String::toLowerCase)
			.map(LEVELS::get)
			.orElse(DEFAULT);
	}

	/**
	 * Checks if a message of this log level should be output when the log level for output is set as given.
	 *
	 * @param outputLevel The set output log level to check
	 * @return true if a message of this log level should be output
	 */
	public boolean shouldLogTo(LogLevel outputLevel) {
		return this.ordinal() >= outputLevel.ordinal();
	}
}
