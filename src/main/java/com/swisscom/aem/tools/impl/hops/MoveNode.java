package com.swisscom.aem.tools.impl.hops;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
public class MoveNode implements Hop<MoveNode.Config> {
	/**
	 * Resolve the path to a new node, creating the parent nodes if necessary.
	 *
	 * @param initialParent the parent node
	 * @param initialTarget the target path
	 * @param conflict      the conflict resolution strategy
	 * @param context       the pipeline context
	 * @return the descriptor of the new node
	 * @throws RepositoryException if an error occurs
	 * @throws HopperException     if an error occurs
	 */
	public static NewNodeDescriptor resolvePathToNewNode(
		Node initialParent,
		String initialTarget,
		ConflictResolution conflict,
		HopContext context
	) throws RepositoryException, HopperException {
		final Session session = initialParent.getSession();
		Node parent = initialParent;
		String target = initialTarget;
		if (target.startsWith("/")) {
			// Absolute path
			parent = session.getRootNode();
		}

		final LinkedList<String> parts = Arrays.stream(target.split("\\/"))
			.filter(StringUtils::isNotEmpty)
			.collect(Collectors.toCollection(LinkedList::new));

		target = parts.removeLast();

		parent = getParentNode(parts, parent, session, target);

		if (parent.hasNode(target)) {
			final Node childNode = parent.getNode(target);
			switch (conflict) {
			case IGNORE:
				context.info("Node {} already exists, won’t replace", childNode.getPath());
				break;
			case FORCE:
				context.info("Replacing existing node {}", childNode.getPath());
				childNode.remove();
				break;
			case THROW:
				throw new HopperException(String.format("Node %s already exists", childNode.getPath()));
			default:
				throw new IllegalArgumentException("Unexpected value: " + conflict);
			}
		}

		return new NewNodeDescriptor(parent, target);
	}

	private static Node getParentNode(List<String> parts, Node startParent, Session session, String target)
		throws HopperException, RepositoryException {
		Node parent = startParent;
		if (!parts.isEmpty()) {
			// Find the new parent node
			final String parentPath = String.join("/", parts);
			if (!parent.hasNode(parentPath)) {
				throw new HopperException(
					String.format(
						"Parent %s/%s for target %s does not exist",
						parent.isSame(session.getRootNode()) ? "" : parent.getPath(),
						parentPath,
						target
					)
				);
			}
			parent = parent.getNode(parentPath);
		}
		return parent;
	}


	@Override
	public void run(MoveNode.Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Cannot move node that has no parent");
			return;
		}

		final String newName = context.evaluateTemplate(config.newName);
		if (StringUtils.equals(newName, "/dev/null")) {
			context.info("Deleting node {}", node.getPath());
			parent.getSession().removeItem(node.getPath());
			return;
		}

		final NewNodeDescriptor descriptor = resolvePathToNewNode(parent, newName, config.conflict, context);
		if (descriptor.getParent().hasNode(descriptor.getNewChildName())) {
			return;
		}

		final String absolutePath = descriptor.getParent().getPath() + '/' + descriptor.getNewChildName();
		context.info("Moving node from {} to {}", node.getPath(), absolutePath);

		node.getSession().move(node.getPath(), absolutePath);
	}

	@Nonnull
	@Override
	public Class<MoveNode.Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "moveNode";
	}

	@RequiredArgsConstructor
	@Getter
	public static class NewNodeDescriptor {

		private final Node parent;
		private final String newChildName;
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
	}
}

