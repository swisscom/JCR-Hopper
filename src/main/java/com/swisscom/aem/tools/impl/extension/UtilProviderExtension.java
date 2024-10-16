package com.swisscom.aem.tools.impl.extension;

import com.swisscom.aem.tools.jcrhopper.RunnerBuilder;
import com.swisscom.aem.tools.jcrhopper.osgi.RunnerBuilderExtension;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

@Component(service = RunnerBuilderExtension.class)
@Slf4j
public class UtilProviderExtension implements RunnerBuilderExtension {

	@Override
	public void configure(RunnerBuilder builder) {
		registerHelper(builder, "io", "org.apache.commons.io.IOUtils");
		registerHelper(builder, "arr", "org.apache.commons.lang3.ArrayUtils");
		registerHelper(builder, "str", "org.apache.commons.lang3.StringUtils");
		registerHelper(builder, "framework", "org.osgi.framework.FrameworkUtil");
	}

	private void registerHelper(RunnerBuilder builder, String name, String className) {
		try {
			final Class<?> klazz = Class.forName(className);
			builder.addUtil(name, klazz);
		} catch (ClassNotFoundException e) {
			log.info("Not registering {} util because class {} is not available", name, className);
		}
	}
}
