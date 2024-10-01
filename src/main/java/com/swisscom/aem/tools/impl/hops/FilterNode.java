package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@Component(service = Hop.class)
public class FilterNode implements Hop<FilterNode.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		if (context.expressionMatches(config.expression)) {
			context.info("Node {} matches filter expression {}", node.getPath(), config.expression);
			context.runHops(node, config.hops);
		}
		else {
			context.debug("Node {} does not match filter expression {}", node.getPath(), config.expression);
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
		return "filterNode";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String expression;
		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}

