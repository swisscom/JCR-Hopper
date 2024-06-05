package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

public class ResolveNode implements ConfigurableAction {
	@Getter
	private final ResolveNodeConfig config;
	private final Pipeline pipeline;

	/**
	 * @param config the configuration
	 */
	public ResolveNode(ResolveNodeConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final String name = context.evaluateTemplate(vars, config.name());
		final Node childNode = context.getJcrFunctions().resolve(node, name);

		if (childNode == null) {
			switch (config.conflict()) {
			case IGNORE:
				if (name.startsWith("/")) {
					context.warn("Could not find node {}. Set conflict to “force” to get rid of this warning.", name);
				} else {
					context.warn("Could not find child node {} of {}. Set conflict to “force” to get rid of this warning.",
						name, node.getPath());
				}
				return;
			case THROW:
				throw new PipelineException(
					name.startsWith("/")
						? String.format("Could not find node %s", name)
						: String.format("Could not find child node %s of %s", name, node.getPath())
				);
			default:
				return;
			}
		}

		context.debug("Selecting node {}", childNode.getPath());
		pipeline.runWith(childNode, vars, context);
	}
}

