package com.swisscom.aem.tools.jcrhopper.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A script that can be run.
 */
@Getter
@ToString
@EqualsAndHashCode
@SuppressFBWarnings(value = "OPM_OVERLY_PERMISSIVE_METHOD", justification = "API surface")
public class Script {
	private final List<HopConfig> hops;
	private final LogLevel logLevel;

	/**
	 * Create a script without hops or parameters and a default log level.
	 * <p>
	 * Useful only for GSON.
	 */
	public Script() {
		this(Collections.emptyList());
	}

	/**
	 * Create a script with the given hops.
	 *
	 * @param hops the hops to configure for this script
	 */
	public Script(List<HopConfig> hops) {
		this(hops, LogLevel.DEFAULT);
	}

	/**
	 * Create a script with the given hops.
	 *
	 * @param hops the hops to configure for this script
	 */
	public Script(HopConfig... hops) {
		this(Arrays.asList(hops));
	}

	/**
	 * Create a script with the given hops and log level.
	 *
	 * @param hops     the hops to configure for this script
	 * @param logLevel the log level verbosity for printed messages
	 */
	public Script(List<HopConfig> hops, LogLevel logLevel) {
		this.hops = hops;
		this.logLevel = logLevel;
	}
}
