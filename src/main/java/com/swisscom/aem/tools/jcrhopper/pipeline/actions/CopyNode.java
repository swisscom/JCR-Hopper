package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.Pipeline;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@Slf4j
public class CopyNode implements ConfigurableAction {
	@Getter
	private final CopyNodeConfig config;
	private final Pipeline pipeline;

	/**
	 * @param config for class
	 */
	public CopyNode(CopyNodeConfig config) {
		this.config = config;
		this.pipeline = new Pipeline(config.actions());
	}

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Copying the root node isn’t allowed");
			return;
		}

		final String newName = context.evaluateTemplate(vars, config.newName());
		final MoveNode.NewNodeDescriptor descriptor = MoveNode.resolvePathToNewNode(parent, newName, config.conflict(), context);
		if (descriptor.getParent().hasNode(descriptor.getNewChildName())) {
			return;
		}

		final String absolutePath = descriptor.getParent().getPath() + '/' + descriptor.getNewChildName();
		context.info("Copying node from {} to {}", node.getPath(), absolutePath);

		final Node copied = copyRecursive(node, descriptor.getParent(), descriptor.getNewChildName(), context);
		pipeline.runWith(copied, vars, context);
	}

	private Node copyRecursive(Node source, Node parent, String targetName, PipelineContext context) throws RepositoryException {
		final Node target = parent.addNode(targetName, source.getPrimaryNodeType().getName());
		for (NodeType mixin : source.getMixinNodeTypes()) {
			target.addMixin(mixin.getName());
		}
		context.debug("Created {} node at {}", target.getPrimaryNodeType().getName(), target.getPath());
		final PropertyIterator propIt = source.getProperties();
		while (propIt.hasNext()) {
			final Property prop = propIt.nextProperty();
			try {
				if (prop.isMultiple()) {
					target.setProperty(prop.getName(), prop.getValues(), prop.getType());
				} else {
					target.setProperty(prop.getName(), prop.getValue(), prop.getType());
				}
			} catch (ConstraintViolationException ex) {
				// Property is protected, assume it’s already covered implicitly by primary or mixin type
				log.debug("Property {} is protected, skipping", prop.getName());
			}
		}
		final NodeIterator nodeIt = source.getNodes();
		while (nodeIt.hasNext()) {
			final Node child = nodeIt.nextNode();
			copyRecursive(child, target, child.getName(), context);
		}
		return target;
	}
}

