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
import org.osgi.service.component.annotations.Component;

@Component(service = Hop.class)
public class ResolveNode implements Hop<ResolveNode.Config> {

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final String name = context.evaluateTemplate(config.name);
		final Node childNode = context.getJcrFunctions().resolve(node, name);

		if (childNode == null) {
			switch (config.conflict) {
				case IGNORE:
					if (name.startsWith("/")) {
						context.warn("Could not find node {}. Set conflict to “force” to get rid of this warning.", name);
					} else {
						context.warn(
							"Could not find child node {} of {}. Set conflict to “force” to get rid of this warning.",
							name,
							node.getPath()
						);
					}
					return;
				case THROW:
					throw new HopperException(
						name.startsWith("/")
							? String.format("Could not find node %s", name)
							: String.format("Could not find child node %s of %s", name, node.getPath())
					);
				default:
					return;
			}
		}

		context.debug("Selecting node {}", childNode.getPath());
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
		return "resolveNode";
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
		private ConflictResolution conflict = ConflictResolution.IGNORE;

		@Nonnull
		private List<HopConfig> hops = Collections.emptyList();
	}
}
