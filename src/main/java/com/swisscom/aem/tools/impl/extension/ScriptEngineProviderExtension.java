package com.swisscom.aem.tools.impl.extension;

import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerBuilderExtension;
import javax.script.ScriptEngineManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(service = RunnerBuilderExtension.class)
public class ScriptEngineProviderExtension implements RunnerBuilderExtension {

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private ScriptEngineManager scriptEngineManager;

	@Override
	public void configure(RunnerBuilder builder) {
		builder.scriptEngineManager(scriptEngineManager);
	}
}
