package com.swisscom.aem.tools.jcrhopper.pipeline.actions;


import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.pipeline.ElExecutor;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class RenameProperty implements ConfigurableAction {
	@Getter
	private final RenamePropertyConfig config;

	@Override
	@SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final ElExecutor elExecutor = context.getElExecutor(vars);
		final String propertyName = elExecutor.evaluateTemplate(config.propertyName());
		final String newName = elExecutor.evaluateTemplate(config.newName());
		if (StringUtils.equals(propertyName, newName)) {
			context.warn("Not renaming property {} on {} as the new name is the same", propertyName, node.getPath());
			return;
		}
		if (shouldStopOnMissingProperty(node, context, propertyName, newName)) {
			return;
		}
		final Property prop = node.getProperty(propertyName);
		if (StringUtils.equals(newName, "/dev/null")) {
			context.info("Deleting property {} from {}", propertyName, node.getPath());
			node.getSession().removeItem(prop.getPath());
			return;
		}
		if (shouldStopOnConflict(node, context, newName, propertyName)) {
			return;
		}
		context.info("Moving property from {} to {}", prop.getPath(), newName);
		if (prop.isMultiple()) {
			node.setProperty(newName, prop.getValues());
		} else {
			node.setProperty(newName, prop.getValue());
		}
		prop.remove();
	}

	private boolean shouldStopOnConflict(
		Node node, PipelineContext context, String newName, String propertyName) throws RepositoryException, PipelineException {
		if (node.hasProperty(newName)) {
			final Property existing = node.getProperty(newName);
			switch (config.conflict()) {
			case ConflictResolution.THROW:
				throw new PipelineException(
					String.format(
						"Property %s could not be renamed to %s because it already exists on %s",
						propertyName,
						newName,
						node.getPath()
					)
				);
			case ConflictResolution.IGNORE:
				context.info("Not replacing existing property {} on {} with value from {}", newName, node.getPath(), propertyName);
				return true;
			case ConflictResolution.FORCE:
				context.info("Replacing existing property {} on {} with value from {}", newName, node.getPath(), propertyName);
				existing.remove();
				return true;
			default:
				throw new IllegalNameException("Invalid conflict name: " + config.conflict().name());
			}
		}
		return false;
	}

	private boolean shouldStopOnMissingProperty(
		Node node, PipelineContext context, String propertyName, String newName) throws RepositoryException, PipelineException {
		if (!node.hasProperty(propertyName)) {
			switch (config.doesNotExist()) {
			case ConflictResolution.THROW:
				throw new PipelineException(
					String.format(
						"Property %s on %s could not be found",
						propertyName,
						newName
					)
				);
			case ConflictResolution.IGNORE:
				context.warn("Property {} on {} does not exist. Set doesNotExist to “force” to avoid this warning",
					propertyName, node.getPath());
				return true;
			default:
				return true;
			}

		}
		return false;
	}
}


