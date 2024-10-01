package com.swisscom.aem.tools.impl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerBuilderExtension;

@Component(service = RunnerBuilderExtension.class)
public class HopProviderExtension implements RunnerBuilderExtension {
	@Reference(
		service = Hop.class,
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		fieldOption = FieldOption.REPLACE
	)
	@SuppressWarnings("PMD.AvoidUsingVolatile")
	private volatile List<Hop<?>> hops;

	@Override
	public void extend(RunnerBuilder builder) {
		builder.addHops(hops);
	}
}
