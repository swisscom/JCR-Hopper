package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@RequiredArgsConstructor
@Component(service = Hop.class)
public class Declare implements Hop<Declare.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		Map<String, String> declarations = config.declarations;
		if (declarations == null) {
			declarations = Collections.emptyMap();
		}
		for (Map.Entry<String, String> declaration : declarations.entrySet()) {
			try {
				final String key = context.evaluateTemplate(declaration.getKey());
				final Object value = context.evaluate(declaration.getValue());
				context.debug("Declaring {}={}", key, value);
				context.set(
					key,
					value
				);
			}
			catch (Exception e) {
				context.error("Error evaluating expression {}", declaration.getValue(), e);
			}
		}
	}


	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "declare";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		@Nonnull
		private Map<String, String> declarations = Collections.emptyMap();
	}
}
