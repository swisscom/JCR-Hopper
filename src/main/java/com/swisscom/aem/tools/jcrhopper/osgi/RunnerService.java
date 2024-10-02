package com.swisscom.aem.tools.jcrhopper.osgi;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.swisscom.aem.tools.impl.extension.HopProviderExtension;
import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.config.Hop;

/**
 * A service to create {@link RunnerBuilder}s extended with configs known to OSGi.
 * <p>
 * By default, the {@link HopProviderExtension} will make all known {@link Hop} services available
 */
@Component(service = RunnerService.class)
public class RunnerService {
	@Reference(
		service = RunnerBuilderExtension.class,
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		fieldOption = FieldOption.REPLACE
	)
	@SuppressWarnings("PMD.AvoidUsingVolatile")
	private volatile List<RunnerBuilderExtension> extensions;

	/**
	 * @return a runner builder extended by all {@link RunnerBuilderExtension} services known to the current OSGi {@link org.osgi.framework.BundleContext}
	 */
	public RunnerBuilder builder() {
		final RunnerBuilder runnerBuilder = Runner.builder();

		for (RunnerBuilderExtension extension : extensions) {
			extension.configure(runnerBuilder);
		}

		return runnerBuilder;
	}
}
