package com.swisscom.aem.tools.impl.hops;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
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

@RequiredArgsConstructor
@Component(service = Hop.class)
public class Declare implements Hop<Declare.Config> {

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Map<String, String> declarations = config.declarations;
		for (Map.Entry<String, String> declaration : declarations.entrySet()) {
			try {
				final String key = context.evaluateTemplate(declaration.getKey());
				final Object value = context.evaluate(declaration.getValue());
				context.debug("Declaring {}={}", key, value);
				context.setVariable(key, value);
			} catch (Exception e) {
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
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {

		@Nonnull
		private Map<String, String> declarations = Collections.emptyMap();
	}
}
