package com.swisscom.aem.tools.jcrhopper.osgi;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.sling.api.resource.ResourceResolver;

public interface SampleContributor {
	/**
	 * Returns a list of sample scripts.
	 *
	 * @param resourceResolver Resource resolver of current scope
	 * @return List of samples with label and configJson
	 */
	List<Sample> getSamples(ResourceResolver resourceResolver);

	@RequiredArgsConstructor
	final class Sample {

		private final String label;
		private final String configJson;
	}
}
