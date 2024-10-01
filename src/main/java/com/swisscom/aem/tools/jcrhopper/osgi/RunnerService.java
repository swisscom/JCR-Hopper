package com.swisscom.aem.tools.jcrhopper.osgi;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.swisscom.aem.tools.jcrhopper.Runner;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;

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

	public RunnerBuilder builder() {
		RunnerBuilder runnerBuilder = Runner.builder();

		for (RunnerBuilderExtension extension : extensions) {
			extension.extend(runnerBuilder);
		}

		return runnerBuilder;
	}
}
