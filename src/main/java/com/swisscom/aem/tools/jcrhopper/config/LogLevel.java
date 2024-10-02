package com.swisscom.aem.tools.jcrhopper.config;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

/**
 * Log level used in scripts.
 */
public enum LogLevel {
	/**
	 * All messages, ideally allows to follow script execution precisely.
	 */
	TRACE,
	/**
	 * Messages only relevant when closely monitoring script execution.
	 */
	DEBUG,
	/**
	 * General information about actions executed.
	 */
	INFO,
	/**
	 * Potential problems.
	 */
	WARN,
	/**
	 * Definite problems.
	 */
	ERROR;

	public static final LogLevel DEFAULT = INFO;

	/**
	 * @param name log level name to look for
	 * @return the log level matching the given name or the default log level
	 */
	@Nonnull
	public static LogLevel fromName(final String name) {
		return Stream.of(values())
			.filter(cr -> StringUtils.equalsIgnoreCase(cr.name(), name))
			.findFirst()
			.orElse(DEFAULT);
	}

	/**
	 * @return The name of the log level
	 */
	public String toName() {
		return StringUtils.lowerCase(this.name());
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
