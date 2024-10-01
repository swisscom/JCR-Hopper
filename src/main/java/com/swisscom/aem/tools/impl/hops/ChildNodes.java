package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@Component(service = Hop.class)
public class ChildNodes implements Hop<ChildNodes.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final NodeIterator childIterator;
		if (StringUtils.isNotBlank(config.namePattern)) {
			final String pattern = context.evaluateTemplate(config.namePattern);
			childIterator = node.getNodes(pattern);
		}
		else {
			childIterator = node.getNodes();
		}

		int counter = 0;
		while (childIterator.hasNext()) {
			final Node childNode = childIterator.nextNode();
			context.debug("Found child node {} on {}", childNode.getName(), node.getPath());
			final Map<String, Object> childVars = new HashMap<>();
			childVars.put(config.counterName, counter);
			childVars.put("queryContext", node);
			context.runHops(childNode, config.hops, childVars);
			counter++;
		}
		if (counter == 0) {
			context.info(
				"Found no child nodes on {}{}",
				node.getPath(),
				StringUtils.isNotBlank(config.namePattern) ? " (matching pattern " + config.namePattern + ")" : ""
			);
		}
	}

	@Nonnull
	@Override
	public Class<ChildNodes.Config> getConfigType() {
		return ChildNodes.Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "childNodes";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String namePattern;
		@Nonnull
		private String counterName = "counter";
		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();

	}
}

