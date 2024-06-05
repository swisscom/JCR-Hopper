package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ElExecutor;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

public class CreateChildNode implements ConfigurableAction {
	@Getter
	private final CreateChildNodeConfig config;
	private final Pipeline pipeline;

	/**
	 * @param config for class
	 */
	public CreateChildNode(CreateChildNodeConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final ElExecutor elExecutor = context.getElExecutor(vars);
		final String name = elExecutor.evaluateTemplate(config.name());
		final String primaryType = elExecutor.evaluateTemplate(config.primaryType());

		final MoveNode.NewNodeDescriptor descriptor = MoveNode.resolvePathToNewNode(node, name, config.conflict(), context);

		final Node childNode;
		if (descriptor.getParent().hasNode(descriptor.getNewChildName())) {
			childNode = descriptor.getParent().getNode(descriptor.getNewChildName());
		} else {
			context.info("Creating new node {} (type {}) under {}",
				descriptor.getNewChildName(), primaryType, descriptor.getParent().getPath());
			childNode = descriptor.getParent().addNode(descriptor.getNewChildName(), primaryType);
		}

		pipeline.runWith(childNode, vars, context);
	}

}

