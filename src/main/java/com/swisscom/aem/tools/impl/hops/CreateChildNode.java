package com.swisscom.aem.tools.impl.hops;

import com.swisscom.aem.tools.jcrhopper.HopperException;
import com.swisscom.aem.tools.jcrhopper.config.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
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
import org.apache.jackrabbit.JcrConstants;
import org.osgi.service.component.annotations.Component;

@Component(service = Hop.class)
public class CreateChildNode implements Hop<CreateChildNode.Config> {

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final String name = context.evaluateTemplate(config.name);
		final String primaryType = context.evaluateTemplate(config.primaryType);

		final MoveNode.NewNodeDescriptor descriptor = MoveNode.resolvePathToNewNode(node, name, config.conflict, context);

		final Node childNode;
		if (descriptor.isTargetExists()) {
			if (!config.runOnExistingNode) {
				return;
			}
			childNode = descriptor.getParent().getNode(descriptor.getNewChildName());
			context.trace("Running the createChild hops on the existing node {}", childNode.getPath());
			if (!childNode.isNodeType(config.primaryType)) {
				context.warn(
					"Existing node {} has type {} but {} was requested",
					childNode.getPath(),
					childNode.getPrimaryNodeType().getName(),
					config.primaryType
				);
			}
		} else {
			context.info(
				"Creating new node {} (type {}) under {}",
				descriptor.getNewChildName(),
				primaryType,
				descriptor.getParent().getPath()
			);
			childNode = descriptor.getParent().addNode(descriptor.getNewChildName(), primaryType);
		}

		context.runHops(childNode, config.hops);
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "createChildNode";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {

		private String name;

		@Nonnull
		private String primaryType = JcrConstants.NT_UNSTRUCTURED;

		@Nonnull
		private ConflictResolution conflict = ConflictResolution.IGNORE;

		private boolean runOnExistingNode;

		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}
