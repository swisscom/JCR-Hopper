package com.swisscom.aem.tools.jcrhopper.pipeline;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public enum ConflictResolution {
	IGNORE,
	FORCE,
	THROW;

	/**
	 * Gets the conflict resolution from the given name.
	 *
	 * @param name The name to get the conflict resolution from
	 * @return The conflict resolution
	 */
	public static ConflictResolution fromName(String name) {
		return Stream.of(ConflictResolution.values())
			.filter(cr -> StringUtils.equalsIgnoreCase(cr.name(), name))
			.findFirst()
			.orElse(null);

	}

	/**
	 * Gets the name of the conflict resolution.
	 *
	 * @return The name of the conflict resolution
	 */
	public String toName() {
		return StringUtils.lowerCase(this.name());
	}
}
