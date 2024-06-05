package com.swisscom.aem.tools.jcrhopper.pipeline;

public interface ConfigurableAction extends Action {
	/**
	 * Gets the configuration for this action.
	 *
	 * @return The configuration
	 */
	Object getConfig();
}
