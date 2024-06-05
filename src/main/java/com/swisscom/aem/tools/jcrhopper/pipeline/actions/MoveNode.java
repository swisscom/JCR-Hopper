package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class MoveNode implements ConfigurableAction {
	@Getter
	private final MoveNodeConfig config;

	/**
	 * Resolve the path to a new node, creating the parent nodes if necessary.
	 *
	 * @param initialParent the parent node
	 * @param initialTarget the target path
	 * @param conflict      the conflict resolution strategy
	 * @param context       the pipeline context
	 * @return the descriptor of the new node
	 * @throws RepositoryException if an error occurs
	 * @throws PipelineException   if an error occurs
	 */
	public static NewNodeDescriptor resolvePathToNewNode(
		Node initialParent, String initialTarget, ConflictResolution conflict, PipelineContext context)
		throws RepositoryException, PipelineException {
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
				throw new PipelineException(String.format("Node %s already exists", childNode.getPath()));
			default:
				throw new IllegalArgumentException("Unexpected value: " + conflict);
			}
		}

		return new NewNodeDescriptor(parent, target);
	}

	private static Node getParentNode(List<String> parts, Node startParent, Session session, String target)
		throws RepositoryException, PipelineException {
		Node parent = startParent;
		if (!parts.isEmpty()) {
			// Find the new parent node
			final String parentPath = String.join("/", parts);
			if (!parent.hasNode(parentPath)) {
				throw new PipelineException(
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
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Cannot move node that has no parent");
			return;
		}

		final String newName = context.evaluateTemplate(vars, config.newName());
		if (StringUtils.equals(newName, "/dev/null")) {
			context.info("Deleting node {}", node.getPath());
			parent.getSession().removeItem(node.getPath());
			return;
		}

		final NewNodeDescriptor descriptor = resolvePathToNewNode(parent, newName, config.conflict(), context);
		if (descriptor.getParent().hasNode(descriptor.getNewChildName())) {
			return;
		}

		final String absolutePath = descriptor.getParent().getPath() + '/' + descriptor.getNewChildName();
		context.info("Moving node from {} to {}", node.getPath(), absolutePath);

		node.getSession().move(node.getPath(), absolutePath);
	}

	@RequiredArgsConstructor
	@Getter
	public static class NewNodeDescriptor {
		private final Node parent;
		private final String newChildName;
	}
}

