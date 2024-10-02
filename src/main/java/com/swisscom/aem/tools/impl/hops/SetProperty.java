package com.swisscom.aem.tools.impl.hops;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import org.osgi.service.component.annotations.Component;

import com.swisscom.aem.tools.jcrhopper.config.ConflictResolution;
import com.swisscom.aem.tools.jcrhopper.config.Hop;
import com.swisscom.aem.tools.jcrhopper.config.HopConfig;
import com.swisscom.aem.tools.jcrhopper.context.HopContext;
import com.swisscom.aem.tools.jcrhopper.HopperException;

@AllArgsConstructor
@Component(service = Hop.class)
public class SetProperty implements Hop<SetProperty.Config> {
	/**
	 * Converts a property to a string representation.
	 *
	 * @param property the property to convert
	 * @param context  the pipeline context
	 * @return the string representation of the property
	 * @throws RepositoryException if an error occurs while reading the property
	 */
	static String stringifyProperty(Property property, HopContext context) throws RepositoryException {
		if (property.isMultiple()) {
			return stringifyValues(property.getValues(), context);
		}
		return property.getString();
	}

	/**
	 * Converts an array of values to a string representation.
	 *
	 * @param values  the values to convert
	 * @param context the pipeline context
	 * @return the string representation of the values
	 */
	static String stringifyValues(Value[] values, HopContext context) {
		return "[" + Arrays.stream(values)
			.map(val -> {
				try {
					return val.getString();
				} catch (RepositoryException e) {
					context.warn(e.getMessage(), e);
				}
				return null;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.joining(", ")) + "]";
	}

	@Override
	public void run(Config config, Node node, HopContext context) throws RepositoryException, HopperException {
		final String propertyName = context.evaluateTemplate(config.propertyName);
		final Object wrappedValue = context.getJcrFunctions().asValueType(context.evaluate(config.value));

		final String valueDebugRepresentation = wrappedValue instanceof Value[]
			? stringifyValues((Value[]) wrappedValue, context)
			: wrappedValue.toString();

		if (node.hasProperty(propertyName)) {
			final Property property = node.getProperty(propertyName);
			final String existing = stringifyProperty(property, context);
			switch (config.conflict) {
			case IGNORE:
				context.info(
					"Not setting new value {} over {} of existing property {} on node {} ",
					valueDebugRepresentation, existing, propertyName, node.getPath()
				);
				return;
			case FORCE:
				context.info(
					"Setting new value {} over {} of existing property {} on node {} ",
					valueDebugRepresentation, existing, propertyName, node.getPath()
				);
				// This is required if we’re converting between single-valued and multivalued property types
				node.getSession().removeItem(property.getPath());
				break;
			case THROW:
				throw new HopperException(
					String.format(
						"Error setting property %s to %s. Property exists on node %s with value %s.",
						propertyName,
						valueDebugRepresentation,
						node.getPath(),
						existing
					)
				);
			default:
				throw new IllegalArgumentException("Unexpected conflict value: " + config.conflict);
			}
		} else {
			context.info("Set value of {} on {} to {}", propertyName, node.getPath(), valueDebugRepresentation);
		}
		if (wrappedValue instanceof Value[]) {
			node.setProperty(propertyName, (Value[]) wrappedValue);
		} else {
			node.setProperty(propertyName, (Value) wrappedValue);
		}
	}

	@Nonnull
	@Override
	public Class<Config> getConfigType() {
		return Config.class;
	}

	@Nonnull
	@Override
	public String getConfigTypeName() {
		return "setProperty";
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@With
	@ToString
	@EqualsAndHashCode
	@SuppressWarnings("PMD.ImmutableField")
	public static final class Config implements HopConfig {
		private String propertyName;
		private String value;
		@Nonnull
		private ConflictResolution conflict = ConflictResolution.FORCE;
	}
}

