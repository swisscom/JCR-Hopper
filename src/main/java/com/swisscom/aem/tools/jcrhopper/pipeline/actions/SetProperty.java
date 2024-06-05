package com.swisscom.aem.tools.jcrhopper.pipeline.actions;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.swisscom.aem.tools.jcrhopper.pipeline.ConfigurableAction;
import com.swisscom.aem.tools.jcrhopper.pipeline.ElExecutor;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineContext;
import com.swisscom.aem.tools.jcrhopper.pipeline.PipelineException;

@AllArgsConstructor
public class SetProperty implements ConfigurableAction {
	@Getter
	private final SetPropertyConfig config;

	/**
	 * Converts a property to a string representation.
	 *
	 * @param property the property to convert
	 * @param context  the pipeline context
	 * @return the string representation of the property
	 * @throws RepositoryException if an error occurs while reading the property
	 */
	static String stringifyProperty(Property property, PipelineContext context) throws RepositoryException {
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
	static String stringifyValues(Value[] values, PipelineContext context) {
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
	public void run(Node node, Map<String, Object> vars, PipelineContext context) throws RepositoryException, PipelineException {
		final ElExecutor elExecutor = context.getElExecutor(vars);
		final String propertyName = elExecutor.evaluateTemplate(config.propertyName());
		final Object wrappedValue = context.getJcrFunctions().asValueType(elExecutor.evaluate(config.value()));

		final String valueDebugRepresentation = wrappedValue instanceof Value[]
			?
			stringifyValues((Value[]) wrappedValue, context)
			:
			wrappedValue.toString();

		if (node.hasProperty(propertyName)) {
			final Property property = node.getProperty(propertyName);
			final String existing = stringifyProperty(property, context);
			switch (config.conflict()) {
			case IGNORE:
				context.info("Not setting new value {} over {} of existing property {} on node {} ",
					valueDebugRepresentation, existing, propertyName, node.getPath());
				return;
			case FORCE:
				context.info("Setting new value {} over {} of existing property {} on node {} ",
					valueDebugRepresentation, existing, propertyName, node.getPath());
				// This is required if we’re converting between single-valued and multi-valued property types
				node.getSession().removeItem(property.getPath());
				break;
			case THROW:
				throw new PipelineException(
					String.format(
						"Error setting property %s to %s. Property exists on node %s with value %s.",
						propertyName,
						valueDebugRepresentation,
						node.getPath(),
						existing
					)
				);
			default:
				throw new IllegalArgumentException("Unexpected value: " + config.conflict());
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
}

