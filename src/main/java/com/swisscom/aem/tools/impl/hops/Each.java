package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.Iterator;
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

@AllArgsConstructor
@Component(service = Hop.class)
public class Each implements Hop<Each.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Object items = context.evaluate(config.expression);
		if (items instanceof Iterable) {
			for (Object item : (Iterable<?>) items) {
				runWith(config, item, node, context);
			}
		} else if (items instanceof Iterator) {
			while (((Iterator<?>) items).hasNext()) {
				runWith(config, ((Iterator<?>) items).next(), node, context);
			}
		} else if (items.getClass().isArray()) {
			for (Object item : (Object[]) items) {
				runWith(config, item, node, context);
			}
		} else {
			runWith(config, items, node, context);
		}
	}

	private void runWith(
		Config config,
		Object item,
		Node initialNode,
		HopContext context
	) throws HopperException, RepositoryException {
		Node node = initialNode;
		if (config.assumeNodes) {
			if (item instanceof Node) {
				node = (Node) item;
			} else if (item instanceof String) {
				node = context.getJcrFunctions().resolve((String) item);
			}
			if (node == null) {
				context.error("Iteration item {} could not be resolved as node", item);
				return;
			}
			context.debug("Iterating node {}", node.getPath());
		} else {
			context.debug("Iterating non-node value {} accessible as {}", item, config.iterator);
		}
		context.runHops(node, config.hops, Collections.singletonMap(config.iterator, item));
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "each";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String expression;
		@Nonnull
		private String iterator = "item";
		private boolean assumeNodes;
		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}
