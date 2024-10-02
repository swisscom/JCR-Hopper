package com.swisscom.aem.tools.jcrhopper.config;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.HopperException;

/**
 * How problems with a hop’s action are handled.
 * <p>
 * There are two distinct scenarios:
 * <ol>
 *     <li>The action can not be executed under any circumstances</li>
 *     <li>Continuance is possible but requires a destructive action (e.g. overriding an existing property)</li>
 * </ol>
 */
public enum ConflictResolution {
	/**
	 * The action is aborted, descendant pipelines are not run.
	 * The current pipeline continues as normal.
	 * The problem is logged.
	 */
	IGNORE,
	/**
	 * The action is forced.
	 * In scenario №1, this means the action is aborted (same as with IGNORE) but no error is logged
	 * In scenario №2, this means doing the destructive action to force continuation and running of the descendant pipeline
	 */
	FORCE,
	/**
	 * The action throws a {@link HopperException}.
	 * The current pipeline is aborted, potentially aborting the entire script execution.
	 */
	THROW;

	/**
	 * Gets the conflict resolution from the given name.
	 *
	 * @param name The name to get the conflict resolution from
	 * @return The conflict resolution
	 */
	public static ConflictResolution fromName(String name) {
		return Stream.of(values())
			.filter(cr -> StringUtils.equalsIgnoreCase(cr.name(), name))
			.findFirst()
			.orElse(null);
	}

	/**
	 * @return The name of the conflict resolution
	 */
	public String toName() {
		return StringUtils.lowerCase(this.name());
	}
}
