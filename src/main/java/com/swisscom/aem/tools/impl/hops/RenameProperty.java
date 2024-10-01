package com.swisscom.aem.tools.impl.hops;


import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;

import com.swisscom.aem.tools.impl.HopContext;
import com.swisscom.aem.tools.jcrhopper.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.Hop;
import com.swisscom.aem.tools.jcrhopper.HopConfig;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@AllArgsConstructor
public class RenameProperty implements Hop<RenameProperty.Config> {
	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final String propertyName = context.evaluateTemplate(config.propertyName);
		final String newName = context.evaluateTemplate(config.newName);
		if (StringUtils.equals(propertyName, newName)) {
			context.warn("Not renaming property {} on {} as the new name is the same", propertyName, node.getPath());
			return;
		}
		if (shouldStopOnMissingProperty(config, node, context, propertyName, newName)) {
			return;
		}
		final Property prop = node.getProperty(propertyName);
		if (StringUtils.equals(newName, "/dev/null")) {
			context.info("Deleting property {} from {}", propertyName, node.getPath());
			node.getSession().removeItem(prop.getPath());
			return;
		}
		if (shouldStopOnConflict(config, node, context, newName, propertyName)) {
			return;
		}
		context.info("Moving property from {} to {}", prop.getPath(), newName);
		if (prop.isMultiple()) {
			node.setProperty(newName, prop.getValues());
		}
		else {
			node.setProperty(newName, prop.getValue());
		}
		prop.remove();
	}

	private boolean shouldStopOnConflict(
		Config config,
		Node node,
		HopContext context,
		String newName,
		String propertyName
	) throws HopperException, RepositoryException {
		if (node.hasProperty(newName)) {
			final Property existing = node.getProperty(newName);
			switch (config.conflict) {
				case THROW:
					throw new HopperException(
						String.format(
							"Property %s could not be renamed to %s because it already exists on %s",
							propertyName,
							newName,
							node.getPath()
						)
					);
				case IGNORE:
					context.info("Not replacing existing property {} on {} with value from {}", newName, node.getPath(), propertyName);
					return true;
				case FORCE:
					context.info("Replacing existing property {} on {} with value from {}", newName, node.getPath(), propertyName);
					existing.remove();
					return true;
				default:
					throw new IllegalNameException("Invalid conflict name: " + config.conflict.name());
			}
		}
		return false;
	}

	private boolean shouldStopOnMissingProperty(
		Config config,
		Node node,
		HopContext context,
		String propertyName,
		String newName
	) throws HopperException, RepositoryException {
		if (!node.hasProperty(propertyName)) {
			switch (config.doesNotExist) {
				case THROW:
					throw new HopperException(
						String.format(
							"Property %s on %s could not be found",
							propertyName,
							newName
						)
					);
				case IGNORE:
					context.warn("Property {} on {} does not exist. Set doesNotExist to “force” to avoid this warning",
						propertyName, node.getPath());
					return true;
				default:
					return true;
			}

		}
		return false;
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "renameProperty";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	public static class Config implements HopConfig {
		private String propertyName;
		private String newName;
		@Nonnull
		private ConflictResolution doesNotExist = ConflictResolution.IGNORE;
		@Nonnull
		private ConflictResolution conflict = ConflictResolution.IGNORE;
	}
}


