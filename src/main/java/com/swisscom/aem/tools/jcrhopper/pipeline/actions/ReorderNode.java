package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class ReorderNode implements ConfigurableAction {
	@Getter
	private final ReorderNodeConfig config;

	@Override
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final Node parent = node.getParent();
		if (parent == null) {
			context.error("Cannot reorder node that has no parent");
			return;
		}

		String beforeNodeName = config.before();
		if (StringUtils.isNotBlank(beforeNodeName)) {
			beforeNodeName = context.evaluateTemplate(vars, config.before());
		}

		if (StringUtils.isBlank(beforeNodeName)) {
			context.info("Making {} last child of {}", node.getName(), parent.getPath());
			beforeNodeName = null;
		} else if (parent.hasNode(beforeNodeName)) {
			context.info("Moving {} before {} in list of child nodes of {}", node.getName(), beforeNodeName, parent.getPath());
		} else {
			switch (config.conflict()) {
			case IGNORE:
				context.warn(
					"Could not find child node {} of {}. Set conflict to “force” to get rid of this warning.",
					beforeNodeName,
					parent.getPath()
				);
				return;
			case THROW:
				throw new PipelineException(String.format(
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
}
