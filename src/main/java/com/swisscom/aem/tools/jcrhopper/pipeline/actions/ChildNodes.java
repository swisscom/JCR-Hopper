package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.DerivedMap;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

public class ChildNodes implements ConfigurableAction {
	@Getter
	private final ChildNodesConfig config;
	private final Pipeline pipeline;

	/**
	 * Constructor.
	 *
	 * @param config for class
	 */
	public ChildNodes(ChildNodesConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final NodeIterator childIterator;
		if (StringUtils.isNotBlank(config.namePattern())) {
			final String pattern = context.evaluateTemplate(vars, config.namePattern());
			childIterator = node.getNodes(pattern);
		} else {
			childIterator = node.getNodes();
		}

		int counter = 0;
		while (childIterator.hasNext()) {
			final Node childNode = childIterator.nextNode();
			context.debug("Found child node {} on {}", childNode.getName(), node.getPath());
			final Map<String, Object> childVars = new DerivedMap<>(vars);
			childVars.put(config.counterName(), counter);
			childVars.put("queryContext", node);
			pipeline.runWith(childNode, childVars, context);
			counter++;
		}
		if (counter == 0) {
			context.info(
				"Found no child nodes on {}{}",
				node.getPath(),
				StringUtils.isNotBlank(config.namePattern()) ? " (matching pattern " + config.namePattern() + ")" : ""
			);
		}
	}
}

