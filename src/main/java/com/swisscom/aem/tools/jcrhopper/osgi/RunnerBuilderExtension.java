package com.swisscom.aem.tools.jcrhopper.osgi;

import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;

/**
 * An OSGi service that configures all {@link RunnerBuilder}s created using {@link RunnerService#builder()}.
 */
public interface RunnerBuilderExtension {
	/**
	 * Configure the given builder.
	 *
	 * @param builder the builder to configure
	 */
	void configure(RunnerBuilder builder);
}
