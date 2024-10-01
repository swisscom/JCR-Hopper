package com.swisscom.aem.tools.impl.hops;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@Slf4j
@Component(service = Hop.class)
public class CopyNode implements Hop<CopyNode.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Copying the root node isn’t allowed");
			return;
		}

		final String newName = context.evaluateTemplate(config.newName);
		final MoveNode.NewNodeDescriptor descriptor = MoveNode.resolvePathToNewNode(parent, newName, config.conflict, context);
		if (descriptor.getParent().hasNode(descriptor.getNewChildName())) {
			return;
		}

		final String absolutePath = descriptor.getParent().getPath() + '/' + descriptor.getNewChildName();
		context.info("Copying node from {} to {}", node.getPath(), absolutePath);

		final Node copied = copyRecursive(node, descriptor.getParent(), descriptor.getNewChildName(), context);
		context.runHops(copied, config.hops);
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	private Node copyRecursive(Node source, Node parent, String targetName, HopContext context) throws RepositoryException {
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
				}
				else {
					target.setProperty(prop.getName(), prop.getValue(), prop.getType());
				}
			}
			catch (ConstraintViolationException ex) {
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

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "copyNode";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String newName;
		@Nonnull
		private ConflictResolution conflict = ConflictResolution.IGNORE;
		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}

