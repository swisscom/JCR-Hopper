package com.swisscom.aem.tools.impl.hops;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@AllArgsConstructor
@Component(service = Hop.class)
public class ReorderNode implements Hop<ReorderNode.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Cannot reorder node that has no parent");
			return;
		}

		String beforeNodeName = config.before;
		if (StringUtils.isNotBlank(beforeNodeName)) {
			beforeNodeName = context.evaluateTemplate(config.before);
		}

		if (StringUtils.isBlank(beforeNodeName)) {
			context.info("Making {} last child of {}", node.getName(), parent.getPath());
			beforeNodeName = null;
		} else if (parent.hasNode(beforeNodeName)) {
			context.info("Moving {} before {} in list of child nodes of {}", node.getName(), beforeNodeName, parent.getPath());
		} else {
			switch (config.conflict) {
			case IGNORE:
				context.warn(
					"Could not find child node {} of {}. Set conflict to “force” to get rid of this warning.",
					beforeNodeName,
					parent.getPath()
				);
				return;
			case THROW:
				throw new HopperException(String.format(
					"Could not find child node %s of %s",
					beforeNodeName,
					parent.getPath()
				));
			default:
				return;
			}
		}

		parent.orderBefore(node.getName(), beforeNodeName);
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "reorderNode";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String before;
		@Nonnull
		private ConflictResolution conflict = ConflictResolution.IGNORE;
	}
}
